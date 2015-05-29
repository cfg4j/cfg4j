/**
 * Copyright 2015 Norbert Potocki (norbert.potocki@nort.pl)
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
      assertThat(gitConfigurationService.getConfiguration()).containsKey("some.setting");
    }
  }
}