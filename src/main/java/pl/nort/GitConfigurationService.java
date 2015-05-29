package pl.nort;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

public class GitConfigurationService implements ConfigurationService {

  private static final String LOCAL_REPOSITORY_PATH_IN_TEMP = "nort-config-git-config-repository";

  /**
   * Read configuration from the remote repository residing at {@code repositoryURI}. Keeps a local
   * clone of the repository in the system tmp directory.
   *
   * @param repositoryURI
   */
  public GitConfigurationService(String repositoryURI) {
    this(repositoryURI, System.getProperty("java.io.tmpdir"), LOCAL_REPOSITORY_PATH_IN_TEMP);
  }

  public GitConfigurationService(String repositoryURI, String tmpPath, String localRepositoryPathInTemp) {

    File clonedRepoPath;

    try {
      clonedRepoPath = File.createTempFile(localRepositoryPathInTemp, "", new File(tmpPath));
      // This folder can't exist or JGit will throw NPE on clone
      if (!clonedRepoPath.delete()) {
        throw new GitConfigurationServiceException("Unable to remove temp directory for local clone: " + localRepositoryPathInTemp);
      }
    } catch (IOException e) {
      throw new GitConfigurationServiceException("Unable to create local clone directory: " + localRepositoryPathInTemp, e);
    }

    Git clonedRepo;

    try {
      clonedRepo = Git.cloneRepository()
          .setURI(repositoryURI)
          .setDirectory(clonedRepoPath)
          .call();
    } catch (GitAPIException e) {
      throw new GitConfigurationServiceException("Unable to clone repository: " + repositoryURI, e);
    }

    clonedRepo.close();
  }

  @Override
  public Properties getConfiguration() {
    return null;
  }

}
