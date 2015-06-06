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

import static org.assertj.core.api.Assertions.assertThat;

import org.assertj.core.data.MapEntry;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class GitConfigurationSourceIntegrationTest {

  @Rule
  public ExpectedException expectedException = ExpectedException.none();

  private TempConfigurationGitRepo remoteRepo;

  @Before
  public void setUp() throws Exception {
    remoteRepo = new TempConfigurationGitRepo();
    remoteRepo.changeProperty("application.properties", "some.setting", "value");
  }

  @After
  public void tearDown() throws Exception {
    remoteRepo.remove();
  }

  @Test
  public void shouldThrowWhenUnableToCreateLocalCloneOnNoTempDir() throws Exception {
    expectedException.expect(GitConfigurationSourceException.class);
    new GitConfigurationSource(remoteRepo.getURI(), "/someNonexistentDir/lkfjalfcz", "existing-path");
  }

  @Test
  public void shouldThrowOnInvalidRemote() throws Exception {
    remoteRepo.remove();
    expectedException.expect(GitConfigurationSourceException.class);
    new GitConfigurationSource(remoteRepo.getURI());
  }

  @Test
  public void shouldReadConfigFromRemoteRepository() throws Exception {
    try (GitConfigurationSource gitConfigurationSource = new GitConfigurationSource(remoteRepo.getURI())) {
      assertThat(gitConfigurationSource.getConfiguration()).contains(MapEntry.entry("some.setting", "value"));
    }
  }

  @Test
  public void refreshShouldSyncWithRepoState() throws Exception {
    try (GitConfigurationSource gitConfigurationSource = new GitConfigurationSource(remoteRepo.getURI())) {
      remoteRepo.changeProperty("application.properties", "some.setting", "otherValue");
      gitConfigurationSource.refresh();

      assertThat(gitConfigurationSource.getConfiguration()).contains(MapEntry.entry("some.setting", "otherValue"));
    }
  }

  @Test
  public void refreshShouldThrowOnSyncProblems() throws Exception {
    try (GitConfigurationSource gitConfigurationSource = new GitConfigurationSource(remoteRepo.getURI())) {
      remoteRepo.remove();

      expectedException.expect(IllegalStateException.class);
      gitConfigurationSource.refresh();
    }
  }
}