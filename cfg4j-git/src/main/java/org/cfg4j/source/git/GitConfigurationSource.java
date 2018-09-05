/*
 * Copyright 2015-2018 Norbert Potocki (norbert.potocki@nort.pl)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.cfg4j.source.git;

import static java.util.Objects.requireNonNull;

import org.cfg4j.source.ConfigurationSource;
import org.cfg4j.source.SourceCommunicationException;
import org.cfg4j.source.context.environment.Environment;
import org.cfg4j.source.context.environment.MissingEnvironmentException;
import org.cfg4j.source.context.filesprovider.ConfigFilesProvider;
import org.cfg4j.source.context.propertiesprovider.PropertiesProvider;
import org.cfg4j.source.context.propertiesprovider.PropertiesProviderSelector;
import org.cfg4j.utils.FileUtils;
import org.eclipse.jgit.api.CheckoutCommand;
import org.eclipse.jgit.api.CreateBranchCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Ref;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Note: use {@link GitConfigurationSourceBuilder} for building instances of this class.
 * <p>
 * Read configuration from the remote GIT repository. Keeps a local clone of the repository.
 */
class GitConfigurationSource implements ConfigurationSource, Closeable {

  private static final Logger LOG = LoggerFactory.getLogger(GitConfigurationSource.class);

  private final BranchResolver branchResolver;
  private final PathResolver pathResolver;
  private final ConfigFilesProvider configFilesProvider;
  private final PropertiesProviderSelector propertiesProviderSelector;
  private final String repositoryURI;
  private final Path tmpPath;
  private final String tmpRepoPrefix;
  private Git clonedRepo;
  private Path clonedRepoPath;
  private boolean initialized;

  /**
   * Note: use {@link GitConfigurationSourceBuilder} for building instances of this class.
   * <p>
   * Read configuration from the remote GIT repository residing at {@code repositoryURI}. Keeps a local
   * clone of the repository in the {@code tmpRepoPrefix} directory under {@code tmpPath} path.
   * Uses provided {@code branchResolver} and {@code pathResolver} for branch and path resolution.
   *
   * @param repositoryURI              URI to the remote git repository
   * @param tmpPath                    path to the tmp directory
   * @param tmpRepoPrefix              prefix for the name of the local directory keeping the repository clone
   * @param branchResolver             {@link BranchResolver} used for extracting git branch from an {@link Environment}
   * @param pathResolver               {@link PathResolver} used for extracting git path from an {@link Environment}
   * @param configFilesProvider        {@link ConfigFilesProvider} used for determining which files in repository should be read
   * @param propertiesProviderSelector selector used for choosing {@link PropertiesProvider} based on a configuration file extension
   *                                   as config files
   */
  GitConfigurationSource(String repositoryURI, Path tmpPath, String tmpRepoPrefix, BranchResolver branchResolver,
                         PathResolver pathResolver, ConfigFilesProvider configFilesProvider,
                         PropertiesProviderSelector propertiesProviderSelector) {
    this.branchResolver = requireNonNull(branchResolver);
    this.pathResolver = requireNonNull(pathResolver);
    this.configFilesProvider = requireNonNull(configFilesProvider);
    this.propertiesProviderSelector = requireNonNull(propertiesProviderSelector);
    this.repositoryURI = requireNonNull(repositoryURI);
    this.tmpPath = requireNonNull(tmpPath);
    this.tmpRepoPrefix = requireNonNull(tmpRepoPrefix);

    initialized = false;
  }

  @Override
  public Properties getConfiguration(Environment environment) {
    if (!initialized) {
      throw new IllegalStateException("Configuration source has to be successfully initialized before you request configuration.");
    }

    reload();

    try {
      checkoutToBranch(branchResolver.getBranchNameFor(environment));
    } catch (GitAPIException e) {
      throw new MissingEnvironmentException(environment.getName(), e);
    }

    Properties properties = new Properties();

    List<Path> paths = new ArrayList<>();
    for (Path path : configFilesProvider.getConfigFiles()) {
      paths.add(clonedRepoPath.resolve(pathResolver.getPathFor(environment)).resolve(path));
    }

    for (Path path : paths) {
      try (InputStream input = new FileInputStream(path.toFile())) {

        PropertiesProvider provider = propertiesProviderSelector.getProvider(path.getFileName().toString());
        properties.putAll(provider.getProperties(input));

      } catch (IOException e) {
        throw new IllegalStateException("Unable to load configuration from " + path.toString() + " file", e);
      }
    }

    return properties;
  }

  /**
   * @throws IllegalStateException        when unable to create directories for local repo clone
   * @throws SourceCommunicationException when unable to clone repository
   */
  @Override
  public void init() {
    LOG.info("Initializing " + GitConfigurationSource.class + " pointing to " + repositoryURI);

    try {
      clonedRepoPath = Files.createTempDirectory(tmpPath, tmpRepoPrefix);
      // This folder can't exist or JGit will throw NPE on clone
      Files.delete(clonedRepoPath);
    } catch (IOException e) {
      throw new IllegalStateException("Unable to create local clone directory: " + tmpRepoPrefix, e);
    }

    try {
      clonedRepo = Git.cloneRepository()
          .setURI(repositoryURI)
          .setDirectory(clonedRepoPath.toFile())
          .call();
    } catch (GitAPIException e) {
      throw new SourceCommunicationException("Unable to clone repository: " + repositoryURI, e);
    }

    initialized = true;
  }

  private void reload() {
    try {
      LOG.debug("Reloading configuration by pulling changes");
      clonedRepo.pull().call();
    } catch (GitAPIException e) {
      initialized = false;
      throw new IllegalStateException("Unable to pull from remote repository", e);
    }
  }

  @Override
  public void close() throws IOException {
    if (clonedRepo != null) {
      LOG.debug("Closing local repository: " + clonedRepoPath);
      clonedRepo.close();
      new FileUtils().deleteDir(clonedRepoPath);
    }
  }

  private void checkoutToBranch(String branch) throws GitAPIException {
    CheckoutCommand checkoutCommand = clonedRepo.checkout()
        .setCreateBranch(false)
        .setName(branch);

    List<Ref> refList = clonedRepo.branchList().call();
    if (!anyRefMatches(refList, branch)) {
      checkoutCommand = checkoutCommand
          .setCreateBranch(true)
          .setUpstreamMode(CreateBranchCommand.SetupUpstreamMode.TRACK)
          .setStartPoint("origin/" + branch);
    }

    checkoutCommand
        .call();
  }

  private boolean anyRefMatches(List<Ref> refList, String branch) {
    for (Ref ref : refList) {
      if (ref.getName().replace("refs/heads/", "").equals(branch)) {
        return true;
      }
    }

    return false;
  }

  @Override
  public String toString() {
    return "GitConfigurationSource{" +
        "clonedRepo=" + clonedRepo +
        ", clonedRepoPath=" + clonedRepoPath +
        ", branchResolver=" + branchResolver +
        ", pathResolver=" + pathResolver +
        ", configFilesProvider=" + configFilesProvider +
        '}';
  }
}
