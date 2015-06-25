/*
 * Copyright 2015 Norbert Potocki (norbert.potocki@nort.pl)
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
import org.cfg4j.source.context.Environment;
import org.cfg4j.source.context.MissingEnvironmentException;
import org.cfg4j.utils.FileUtils;
import org.eclipse.jgit.api.CheckoutCommand;
import org.eclipse.jgit.api.CreateBranchCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Ref;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class GitConfigurationSource implements ConfigurationSource, Closeable {

  private static final Logger LOG = LoggerFactory.getLogger(GitConfigurationSource.class);

  private final Git clonedRepo;
  private final File clonedRepoPath;
  private final BranchResolver branchResolver;
  private final PathResolver pathResolver;
  private final ConfigFilesProvider configFilesProvider;

  /**
   * Note: use {@link GitConfigurationSourceBuilder} for building instances of this class.
   * <p>
   * Read configuration from the remote GIT repository residing at {@code repositoryURI}. Keeps a local
   * clone of the repository in the {@code localRepositoryPathInTemp} directory under {@code tmpPath} path.
   * Uses provided {@code branchResolver} and {@code pathResolver} for branch and path resolution.
   *
   * @param repositoryURI             URI to the remote git repository
   * @param tmpPath                   path to the tmp directory
   * @param localRepositoryPathInTemp name of the local directory keeping the repository clone
   * @param branchResolver            {@link BranchResolver} used for extracting git branch from an {@link Environment}
   * @param pathResolver              {@link PathResolver} used for extracting git path from an {@link Environment}
   * @param configFilesProvider       {@link ConfigFilesProvider} used for determining which files in repository should be read
   *                                  as config files
   * @throws GitConfigurationSourceException when unable to clone repository
   */
  GitConfigurationSource(String repositoryURI, String tmpPath, String localRepositoryPathInTemp, BranchResolver branchResolver,
                         PathResolver pathResolver, ConfigFilesProvider configFilesProvider) {
    this.branchResolver = requireNonNull(branchResolver);
    this.pathResolver = requireNonNull(pathResolver);
    this.configFilesProvider = requireNonNull(configFilesProvider);
    requireNonNull(tmpPath);
    requireNonNull(localRepositoryPathInTemp);
    requireNonNull(repositoryURI);

    LOG.info("Initializing " + GitConfigurationSource.class + " pointing to " + repositoryURI);

    try {
      clonedRepoPath = File.createTempFile(localRepositoryPathInTemp, "", new File(tmpPath));
      // This folder can't exist or JGit will throw NPE on clone
      if (!clonedRepoPath.delete()) {
        throw new GitConfigurationSourceException("Unable to remove temp directory for local clone: " + localRepositoryPathInTemp);
      }
    } catch (IOException e) {
      throw new GitConfigurationSourceException("Unable to create local clone directory: " + localRepositoryPathInTemp, e);
    }

    try {
      clonedRepo = Git.cloneRepository()
          .setURI(repositoryURI)
          .setDirectory(clonedRepoPath)
          .call();
    } catch (GitAPIException e) {
      throw new GitConfigurationSourceException("Unable to clone repository: " + repositoryURI, e);
    }
  }

  @Override
  public Properties getConfiguration(Environment environment) {
    try {
      checkoutToBranch(branchResolver.getBranchNameFor(environment));
    } catch (GitAPIException e) {
      throw new MissingEnvironmentException(environment.getName(), e);
    }

    Properties properties = new Properties();

    List<File> files = StreamSupport.stream(configFilesProvider.getConfigFiles().spliterator(), false)
        .map(file -> new File(clonedRepoPath + "/" + pathResolver.getPathFor(environment) + "/" + file.getPath()))
        .collect(Collectors.toList());

    for (File file : files) {
      try (InputStream input = new FileInputStream(file.getPath())) {
        properties.load(input);
      } catch (IOException | IllegalArgumentException e) {
        throw new IllegalStateException("Unable to load properties from application.properties file", e);
      }
    }

    return properties;
  }

  @Override
  public void reload() {
    try {
      LOG.debug("Refreshing configuration by pulling changes");
      clonedRepo.pull().call();
    } catch (GitAPIException e) {
      throw new IllegalStateException("Unable to pull from remote repository", e);
    }
  }

  @Override
  public void close() throws IOException {
    LOG.debug("Closing local repository: " + clonedRepoPath);
    clonedRepo.close();
    FileUtils.deleteDir(clonedRepoPath);
  }

  private void checkoutToBranch(String branch) throws GitAPIException {
    CheckoutCommand checkoutCommand = clonedRepo.checkout()
        .setCreateBranch(false)
        .setName(branch);

    List<Ref> refList = clonedRepo.branchList().call();
    if (!refList.stream().anyMatch(ref -> ref.getName().replace("refs/heads/", "").equals(branch))) {
      checkoutCommand = checkoutCommand
          .setCreateBranch(true)
          .setUpstreamMode(CreateBranchCommand.SetupUpstreamMode.TRACK)
          .setStartPoint("origin/" + branch);
    }

    checkoutCommand
        .call();
  }
}
