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
package pl.nort.config.source;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.nort.config.source.context.EnvSelectionStrategy;
import pl.nort.config.utils.FileUtils;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class GitConfigurationSource implements ConfigurationSource, Closeable {

  private static final Logger LOG = LoggerFactory.getLogger(GitConfigurationSource.class);
  private static final String LOCAL_REPOSITORY_PATH_IN_TEMP = "nort-config-git-config-repository";

  private final Git clonedRepo;
  private final File clonedRepoPath;

  /**
   * Read configuration from the remote GIT repository residing at {@code repositoryURI}. Keeps a local
   * clone of the repository in the system tmp directory.
   *
   * @param repositoryURI URI to the remote git repository
   * @throws GitConfigurationSourceException when unable to clone repository
   */
  public GitConfigurationSource(String repositoryURI) {
    this(repositoryURI, System.getProperty("java.io.tmpdir"), LOCAL_REPOSITORY_PATH_IN_TEMP);
  }

  /**
   * Read configuration from the remote GIT repository residing at {@code repositoryURI}. Keeps a local
   * clone of the repository in the {@code localRepositoryPathInTemp} directory under {@code tmpPath} path.
   *
   * @param repositoryURI             URI to the remote git repository
   * @param tmpPath                   path to the tmp directory
   * @param localRepositoryPathInTemp name of the local directory keeping the repository clone
   * @throws GitConfigurationSourceException when unable to clone repository
   */
  public GitConfigurationSource(String repositoryURI, String tmpPath, String localRepositoryPathInTemp) {

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
  public Properties getConfiguration() {
    Properties properties = new Properties();
    InputStream input = null;

    try {
      input = new FileInputStream(clonedRepoPath + "/application.properties");
      properties.load(input);
    } catch (IOException ex) {
      ex.printStackTrace();
    } finally {
      if (input != null) {
        try {
          input.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }

    return properties;
  }

  @Override
  public Properties getConfiguration(EnvSelectionStrategy envSelectionStrategy) {
    return null;
  }

  @Override
  public void refresh() {
    try {
      LOG.debug("Refreshing configuration by pulling changes from branch: " + clonedRepo.getRepository().getBranch());
      clonedRepo.pull().call();
    } catch (GitAPIException | IOException e) {
      throw new IllegalStateException("Unable to pull from remote repository", e);
    }
  }

  @Override
  public void close() throws IOException {
    LOG.debug("Closing local repository: " + clonedRepoPath);
    clonedRepo.close();
    FileUtils.deleteDir(clonedRepoPath);
  }
}
