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

import static org.assertj.core.api.Assertions.assertThat;

import org.assertj.core.data.MapEntry;
import org.cfg4j.source.SourceCommunicationException;
import org.cfg4j.source.context.DefaultEnvironment;
import org.cfg4j.source.context.Environment;
import org.cfg4j.source.context.ImmutableEnvironment;
import org.cfg4j.source.context.MissingEnvironmentException;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;

public class GitConfigurationSourceIntegrationTest {

  public static final String DEFAULT_BRANCH = "master";
  public static final String TEST_ENV_BRANCH = "testEnvBranch";

  @Rule
  public ExpectedException expectedException = ExpectedException.none();

  private TempConfigurationGitRepo remoteRepo;
  private FileSystem fileSystem;

  @Before
  public void setUp() throws Exception {
    remoteRepo = new TempConfigurationGitRepo("cfg4j-test-repo.git");
    remoteRepo.changeProperty("application.properties", "some.setting", "masterValue");
    remoteRepo.changeProperty("otherConfig.properties", "otherConfig.setting", "masterValue");
    remoteRepo.changeProperty("malformed.properties", "otherConfig.setting", "\\uzzzzz");
    remoteRepo.changeProperty("otherApplicationConfigs/application.properties", "some.setting", "otherAppSetting");

    remoteRepo.changeBranchTo(TEST_ENV_BRANCH);
    remoteRepo.changeProperty("application.properties", "some.setting", "testValue");

    remoteRepo.changeBranchTo(DEFAULT_BRANCH);

    fileSystem = FileSystems.getDefault();
  }

  @After
  public void tearDown() throws Exception {
    remoteRepo.remove();
  }

  @Test
  public void shouldThrowWhenUnableToCreateLocalCloneOnNoTempDir() throws Exception {
    expectedException.expect(IllegalStateException.class);

    getSourceBuilderForRemoteRepoWithDefaults()
        .withTmpPath("/someNonexistentDir/lkfjalfcz")
        .withLocalRepositoryPathInTemp("existing-path")
        .build();
  }

  @Test
  public void shouldThrowOnInvalidRemote() throws Exception {
    remoteRepo.remove();
    expectedException.expect(SourceCommunicationException.class);
    getSourceForRemoteRepoWithDefaults();
  }

  @Test
  public void getConfiguration2ShouldUseBranchResolver() throws Exception {
    class Resolver implements BranchResolver {

      @Override
      public String getBranchNameFor(Environment environment) {
        return TEST_ENV_BRANCH;
      }
    }

    try (GitConfigurationSource gitConfigurationSource = getSourceForRemoteRepoWithBranchResolver(new Resolver())) {
      Environment environment = new ImmutableEnvironment("ignoreMePlease");

      assertThat(gitConfigurationSource.getConfiguration(environment)).contains(MapEntry.entry("some.setting", "testValue"));
    }
  }

  @Test
  public void getConfiguration2ShouldReadConfigFromGivenBranch() throws Exception {
    try (GitConfigurationSource gitConfigurationSource = getSourceForRemoteRepoWithDefaults()) {
      Environment environment = new ImmutableEnvironment(TEST_ENV_BRANCH);

      assertThat(gitConfigurationSource.getConfiguration(environment)).contains(MapEntry.entry("some.setting", "testValue"));
    }
  }

  @Test
  public void getConfiguration2ShouldUsePathResolver() throws Exception {
    class Resolver implements PathResolver {

      @Override
      public Path getPathFor(Environment environment) {
        return fileSystem.getPath("otherApplicationConfigs");
      }
    }

    try (GitConfigurationSource gitConfigurationSource = getSourceForRemoteRepoWithPathResolver(new Resolver())) {
      Environment environment = new DefaultEnvironment();

      assertThat(gitConfigurationSource.getConfiguration(environment)).contains(MapEntry.entry("some.setting", "otherAppSetting"));
    }
  }

  @Test
  public void getConfiguration2ShouldReadFromGivenPath() throws Exception {
    try (GitConfigurationSource gitConfigurationSource = getSourceForRemoteRepoWithDefaults()) {
      Environment environment = new ImmutableEnvironment("/otherApplicationConfigs/");

      assertThat(gitConfigurationSource.getConfiguration(environment)).contains(MapEntry.entry("some.setting", "otherAppSetting"));
    }
  }

  @Test
  public void getConfiguration2ShouldReadFromGivenFiles() throws Exception {
    ConfigFilesProvider configFilesProvider = () -> Arrays.asList(fileSystem.getPath("application.properties"), fileSystem.getPath("otherConfig.properties"));
    Environment environment = new DefaultEnvironment();

    try (GitConfigurationSource gitConfigurationSource = getSourceForRemoteRepoWithFilesProvider(configFilesProvider)) {
      assertThat(gitConfigurationSource.getConfiguration(environment)).containsKeys("some.setting", "otherConfig.setting");
    }
  }

  @Test
  public void getConfiguration2ShouldThrowOnMissingBranch() throws Exception {
    try (GitConfigurationSource gitConfigurationSource = getSourceForRemoteRepoWithDefaults()) {
      expectedException.expect(MissingEnvironmentException.class);
      gitConfigurationSource.getConfiguration(new ImmutableEnvironment("nonExistentBranch"));
    }
  }

  @Test
  public void getConfiguration2ShouldThrowOnMissingConfigFile() throws Exception {
    remoteRepo.deleteFile("application.properties");

    try (GitConfigurationSource gitConfigurationSource = getSourceForRemoteRepoWithDefaults()) {
      expectedException.expect(IllegalStateException.class);
      gitConfigurationSource.getConfiguration(new DefaultEnvironment());
    }
  }

  @Test
  public void getConfiguration2ShouldThrowOnMalformedConfigFile() throws Exception {
    ConfigFilesProvider configFilesProvider = () -> Collections.singletonList(fileSystem.getPath("malformed.properties"));

    expectedException.expect(IllegalStateException.class);
    getSourceForRemoteRepoWithFilesProvider(configFilesProvider).getConfiguration(new DefaultEnvironment());
  }

  @Test
  public void refreshShouldUpdateGetConfiguration2OnDefaultBranch() throws Exception {
    try (GitConfigurationSource gitConfigurationSource = getSourceForRemoteRepoWithDefaults()) {
      remoteRepo.changeProperty("application.properties", "some.setting", "changedValue");
      gitConfigurationSource.reload();

      assertThat(gitConfigurationSource.getConfiguration(new DefaultEnvironment())).contains(MapEntry.entry("some.setting", "changedValue"));
    }
  }

  @Test
  public void refreshShouldUpdateGetConfiguration2OnNonDefaultBranch() throws Exception {
    try (GitConfigurationSource gitConfigurationSource = getSourceForRemoteRepoWithDefaults()) {
      remoteRepo.changeBranchTo(TEST_ENV_BRANCH);
      remoteRepo.changeProperty("application.properties", "some.setting", "changedValue");
      gitConfigurationSource.reload();

      assertThat(gitConfigurationSource.getConfiguration(new ImmutableEnvironment(TEST_ENV_BRANCH))).contains(MapEntry.entry("some.setting", "changedValue"));
    }
  }

  @Test
  public void refreshShouldThrowOnSyncProblems() throws Exception {
    try (GitConfigurationSource gitConfigurationSource = getSourceForRemoteRepoWithDefaults()) {
      remoteRepo.remove();

      expectedException.expect(IllegalStateException.class);
      gitConfigurationSource.reload();
    }
  }

  private GitConfigurationSource getSourceForRemoteRepoWithDefaults() {
    return getSourceBuilderForRemoteRepoWithDefaults().build();
  }

  private GitConfigurationSource getSourceForRemoteRepoWithBranchResolver(BranchResolver branchResolver) {
    return getSourceBuilderForRemoteRepoWithDefaults()
        .withBranchResolver(branchResolver)
        .build();
  }

  private GitConfigurationSource getSourceForRemoteRepoWithPathResolver(PathResolver pathResolver) {
    return getSourceBuilderForRemoteRepoWithDefaults()
        .withPathResolver(pathResolver)
        .build();
  }

  private GitConfigurationSource getSourceForRemoteRepoWithFilesProvider(ConfigFilesProvider configFilesProvider) {
    return getSourceBuilderForRemoteRepoWithDefaults()
        .withConfigFilesProvider(configFilesProvider)
        .build();
  }

  private GitConfigurationSourceBuilder getSourceBuilderForRemoteRepoWithDefaults() {
    return new GitConfigurationSourceBuilder()
        .withRepositoryURI(remoteRepo.getURI());
  }
}