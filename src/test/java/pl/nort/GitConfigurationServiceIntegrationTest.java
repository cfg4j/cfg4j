package pl.nort;

import static org.fest.assertions.api.Assertions.assertThat;

import org.junit.Test;

public class GitConfigurationServiceIntegrationTest {

  @Test
  public void shouldReadConfigFromRemoteRepository() throws Exception {
    String repoCoordinates = "git@github.com:nort/config-git-sample-config.git";
    GitConfigurationService gitConfigurationService = new GitConfigurationService(repoCoordinates);

    assertThat(gitConfigurationService.getConfiguration()).isNotEmpty();
  }
}