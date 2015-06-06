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
import pl.nort.config.source.context.EnvSelectionStrategy;
import pl.nort.config.source.context.ImmutableEnvSelectionStrategy;
import pl.nort.config.source.context.MissingEnvironmentException;

public class GitConfigurationSourceIntegrationTest {

  public static final String DEFAULT_BRANCH = "master";
  public static final String TEST_ENV_BRANCH = "testEnvBranch";

  @Rule
  public ExpectedException expectedException = ExpectedException.none();

  private TempConfigurationGitRepo remoteRepo;

  @Before
  public void setUp() throws Exception {
    remoteRepo = new TempConfigurationGitRepo();
    remoteRepo.changeProperty("application.properties", "some.setting", "masterValue");

    remoteRepo.changeBranchTo(TEST_ENV_BRANCH);
    remoteRepo.changeProperty("application.properties", "some.setting", "testValue");

    remoteRepo.changeBranchTo(DEFAULT_BRANCH);
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
  public void getConfigurationShouldReadConfigFromRemoteRepository() throws Exception {
    try (GitConfigurationSource gitConfigurationSource = new GitConfigurationSource(remoteRepo.getURI())) {
      assertThat(gitConfigurationSource.getConfiguration()).contains(MapEntry.entry("some.setting", "masterValue"));
    }
  }

  @Test
  public void getConfigurationShouldThrowOnMissingConfigFile() throws Exception {
    remoteRepo.deleteFile("application.properties");

    try (GitConfigurationSource gitConfigurationSource = new GitConfigurationSource(remoteRepo.getURI())) {
      expectedException.expect(IllegalStateException.class);
      gitConfigurationSource.getConfiguration();
    }
  }

  @Test
  public void getConfigurationShouldThrowOnMissingBranch() throws Exception {
    remoteRepo.changeBranchTo("test");
    remoteRepo.deleteBranch(DEFAULT_BRANCH);

    try (GitConfigurationSource gitConfigurationSource = new GitConfigurationSource(remoteRepo.getURI())) {
      expectedException.expect(IllegalStateException.class);
      gitConfigurationSource.getConfiguration();
    }
  }

  @Test
  public void getConfiguration2ShouldReadConfigFromSpecifiedBranch() throws Exception {
    try (GitConfigurationSource gitConfigurationSource = new GitConfigurationSource(remoteRepo.getURI())) {
      EnvSelectionStrategy selectionStrategy = new ImmutableEnvSelectionStrategy(TEST_ENV_BRANCH);

      assertThat(gitConfigurationSource.getConfiguration(selectionStrategy)).contains(MapEntry.entry("some.setting", "testValue"));
    }
  }

  @Test
  public void getConfiguration2ShouldThrowOnMissingBranch() throws Exception {
    try (GitConfigurationSource gitConfigurationSource = new GitConfigurationSource(remoteRepo.getURI())) {
      expectedException.expect(MissingEnvironmentException.class);
      gitConfigurationSource.getConfiguration(new ImmutableEnvSelectionStrategy("nonExistentBranch"));
    }
  }

  @Test
  public void getConfiguration2ShouldThrowOnMissingConfigFile() throws Exception {
    remoteRepo.deleteFile("application.properties");

    try (GitConfigurationSource gitConfigurationSource = new GitConfigurationSource(remoteRepo.getURI())) {
      expectedException.expect(IllegalStateException.class);
      gitConfigurationSource.getConfiguration(new ImmutableEnvSelectionStrategy(DEFAULT_BRANCH));
    }
  }

  @Test
  public void refreshShouldUpdateGetConfigurationResults() throws Exception {
    try (GitConfigurationSource gitConfigurationSource = new GitConfigurationSource(remoteRepo.getURI())) {
      remoteRepo.changeProperty("application.properties", "some.setting", "changedValue");
      gitConfigurationSource.refresh();

      assertThat(gitConfigurationSource.getConfiguration()).contains(MapEntry.entry("some.setting", "changedValue"));
    }
  }

  @Test
  public void refreshShouldUpdateGetConfiguration2OnDefaultBranch() throws Exception {
    try (GitConfigurationSource gitConfigurationSource = new GitConfigurationSource(remoteRepo.getURI())) {
      remoteRepo.changeProperty("application.properties", "some.setting", "changedValue");
      gitConfigurationSource.refresh();

      assertThat(gitConfigurationSource.getConfiguration(new ImmutableEnvSelectionStrategy(DEFAULT_BRANCH))).contains(MapEntry.entry("some.setting", "changedValue"));
    }
  }

  @Test
  public void refreshShouldUpdateGetConfiguration2OnNonDefaultBranch() throws Exception {
    try (GitConfigurationSource gitConfigurationSource = new GitConfigurationSource(remoteRepo.getURI())) {
      remoteRepo.changeBranchTo(TEST_ENV_BRANCH);
      remoteRepo.changeProperty("application.properties", "some.setting", "changedValue");
      gitConfigurationSource.refresh();

      assertThat(gitConfigurationSource.getConfiguration(new ImmutableEnvSelectionStrategy(TEST_ENV_BRANCH))).contains(MapEntry.entry("some.setting", "changedValue"));
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