package pl.nort;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

public class GitConfigurationService implements ConfigurationService {

  private static final String LOCAL_REPOSITORY_PATH_IN_TEMP = "nort-config-git-config-repository";

  public GitConfigurationService(String repositoryURI) throws GitAPIException {
    this(repositoryURI, System.getProperty("java.io.tmpdir"), LOCAL_REPOSITORY_PATH_IN_TEMP);
  }

  public GitConfigurationService(String repositoryURI, String tmpPath, String localRepositoryPathInTemp) throws GitAPIException {

    File clonedRepoPath;

    try {
      clonedRepoPath = File.createTempFile(localRepositoryPathInTemp, "", new File(tmpPath));
    } catch (IOException e) {
      throw new GitConfigurationServiceException("Unable to create local clone directory: " + localRepositoryPathInTemp, e);
    }

    Git.cloneRepository()
        .setURI(repositoryURI)
        .setDirectory(clonedRepoPath)
        .call();
  }

  @Override
  public Properties getConfiguration() {
    return null;
  }

}
