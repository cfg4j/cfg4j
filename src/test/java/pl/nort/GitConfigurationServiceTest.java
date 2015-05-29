package pl.nort;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class GitConfigurationServiceTest {

  @Rule
  public ExpectedException expectedException = ExpectedException.none();

  @Test
  public void shouldThrowWhenUnableToCreateLocalCloneOnNoTempDir() throws Exception {
    System.setProperty("java.io.tmpdir", "/someCrazyDir/lkfjalfcz");

    expectedException.expect(GitConfigurationServiceException.class);
    new GitConfigurationService("https://github.com/nort/config-git-sample-config.git", "existing-path");
  }
}