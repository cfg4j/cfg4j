package pl.nort;

import static org.fest.assertions.api.Assertions.assertThat;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class GitConfigurationServiceIntegrationTest {

  @Rule
  public ExpectedException expectedException = ExpectedException.none();

  @Test
  public void shouldThrowWhenUnableToCreateLocalCloneOnNoTempDir() throws Exception {
    expectedException.expect(GitConfigurationServiceException.class);
    new GitConfigurationService("https://github.com/nort/config-git-sample-config.git", "/someNonexistentDir/lkfjalfcz", "existing-path");
  }

  @Test
  public void shouldThrowOnInvalidRemote() throws Exception {
    expectedException.expect(GitConfigurationServiceException.class);
    new GitConfigurationService("https://github.com/nort/nonExistentRepo");
  }

  @Test
  public void shouldReadConfigFromRemoteRepository() throws Exception {
    String repoCoordinates = "https://github.com/nort/config-git-sample-config.git";

    try (GitConfigurationService gitConfigurationService = new GitConfigurationService(repoCoordinates)) {
      assertThat(gitConfigurationService.getConfiguration()).isNotEmpty();
    }
  }
}