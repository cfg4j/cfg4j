/*
 * Copyright 2015-2016 Norbert Potocki (norbert.potocki@nort.pl)
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
import org.cfg4j.source.context.environment.DefaultEnvironment;
import org.cfg4j.source.context.environment.Environment;
import org.cfg4j.source.context.environment.ImmutableEnvironment;
import org.cfg4j.source.context.environment.MissingEnvironmentException;
import org.cfg4j.source.context.filesprovider.ConfigFilesProvider;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;

public class GitConfigurationSourceIntegrationTest {

  private static final String DEFAULT_BRANCH = "master";
  private static final String TEST_ENV_BRANCH = "testEnvBranch";

  @Rule
  public ExpectedException expectedException = ExpectedException.none();

  private TempConfigurationGitRepo remoteRepo;

  @Before
  public void setUp() throws Exception {
    remoteRepo = new TempConfigurationGitRepo("org.cfg4j-test-repo.git");
    remoteRepo.changeProperty(Paths.get("application.properties"), "some.setting", "masterValue");
    remoteRepo.changeProperty(Paths.get("otherConfig.properties"), "otherConfig.setting", "masterValue");
    remoteRepo.changeProperty(Paths.get("malformed.properties"), "otherConfig.setting", "\\uzzzzz");
    remoteRepo.changeProperty(Paths.get("otherApplicationConfigs/application.properties"), "some.setting", "otherAppSetting");

    remoteRepo.changeBranchTo(TEST_ENV_BRANCH);
    remoteRepo.changeProperty(Paths.get("application.properties"), "some.setting", "testValue");

    remoteRepo.changeBranchTo(DEFAULT_BRANCH);
  }

  @After
  public void tearDown() throws Exception {
    remoteRepo.remove();
  }

  @Test
  public void initThrowsWhenUnableToCreateLocalCloneOnNoTempDir() throws Exception {
    expectedException.expect(IllegalStateException.class);

    getSourceBuilderForRemoteRepoWithDefaults()
        .withTmpPath(Paths.get("/someNonexistentDir/lkfjalfcz"))
        .withTmpRepoPrefix("existing-path")
        .build()
        .init();
  }

  @Test
  public void initThrowsOnInvalidRemote() throws Exception {
    expectedException.expect(SourceCommunicationException.class);
    new GitConfigurationSourceBuilder()
        .withRepositoryURI("")
        .build()
        .init();
  }

  @Test
  public void getConfigurationUsesBranchResolver() throws Exception {
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
  public void getConfigurationReadsConfigFromGivenBranch() throws Exception {
    try (GitConfigurationSource gitConfigurationSource = getSourceForRemoteRepoWithDefaults()) {
      Environment environment = new ImmutableEnvironment(TEST_ENV_BRANCH);

      assertThat(gitConfigurationSource.getConfiguration(environment)).contains(MapEntry.entry("some.setting", "testValue"));
    }
  }

  @Test
  public void getConfigurationUsesPathResolver() throws Exception {
    class Resolver implements PathResolver {

      @Override
      public Path getPathFor(Environment environment) {
        return Paths.get("otherApplicationConfigs");
      }
    }

    try (GitConfigurationSource gitConfigurationSource = getSourceForRemoteRepoWithPathResolver(new Resolver())) {
      Environment environment = new DefaultEnvironment();

      assertThat(gitConfigurationSource.getConfiguration(environment)).contains(MapEntry.entry("some.setting", "otherAppSetting"));
    }
  }

  @Test
  public void getConfigurationReadsFromGivenPath() throws Exception {
    try (GitConfigurationSource gitConfigurationSource = getSourceForRemoteRepoWithDefaults()) {
      Environment environment = new ImmutableEnvironment("/otherApplicationConfigs/");

      assertThat(gitConfigurationSource.getConfiguration(environment)).contains(MapEntry.entry("some.setting", "otherAppSetting"));
    }
  }

  @Test
  public void getConfigurationReadsFromGivenFiles() throws Exception {
    ConfigFilesProvider configFilesProvider = new ConfigFilesProvider() {
      @Override
      public Iterable<Path> getConfigFiles() {
        return Arrays.asList(Paths.get("application.properties"), Paths.get("otherConfig.properties"));
      }
    };
    Environment environment = new DefaultEnvironment();

    try (GitConfigurationSource gitConfigurationSource = getSourceForRemoteRepoWithFilesProvider(configFilesProvider)) {
      assertThat(gitConfigurationSource.getConfiguration(environment)).containsKeys("some.setting", "otherConfig.setting");
    }
  }

  @Test
  public void getConfigurationThrowsOnMissingBranch() throws Exception {
    try (GitConfigurationSource gitConfigurationSource = getSourceForRemoteRepoWithDefaults()) {
      expectedException.expect(MissingEnvironmentException.class);
      gitConfigurationSource.getConfiguration(new ImmutableEnvironment("nonExistentBranch"));
    }
  }

  @Test
  public void getConfigurationThrowsOnMissingConfigFile() throws Exception {
    remoteRepo.deleteFile(Paths.get("application.properties"));

    try (GitConfigurationSource gitConfigurationSource = getSourceForRemoteRepoWithDefaults()) {
      expectedException.expect(IllegalStateException.class);
      gitConfigurationSource.getConfiguration(new DefaultEnvironment());
    }
  }

  @Test
  public void getConfigurationThrowsOnMalformedConfigFile() throws Exception {
    ConfigFilesProvider configFilesProvider = new ConfigFilesProvider() {
      @Override
      public Iterable<Path> getConfigFiles() {
        return Collections.singletonList(Paths.get("malformed.properties"));
      }
    };

    expectedException.expect(IllegalStateException.class);
    getSourceForRemoteRepoWithFilesProvider(configFilesProvider).getConfiguration(new DefaultEnvironment());
  }

  @Test
  public void getConfigurationThrowsBeforeInitCalled() throws Exception {
    try (GitConfigurationSource gitConfigurationSource = getSourceBuilderForRemoteRepoWithDefaults().build()) {
      expectedException.expect(IllegalStateException.class);
      gitConfigurationSource.getConfiguration(new ImmutableEnvironment(""));
    }
  }

  @Test
  public void getConfigurationWithCredentials() throws Exception {
    try (GitConfigurationSource gitConfigurationSource = getSourceForRemoteRepoWithBlankCredentials()) {
      Environment environment = new DefaultEnvironment();
      assertThat(gitConfigurationSource.getConfiguration(environment)).contains(MapEntry.entry("some.setting", "masterValue"));
    }
  }

  private GitConfigurationSource getSourceForRemoteRepoWithDefaults() {
    GitConfigurationSource source = getSourceBuilderForRemoteRepoWithDefaults().build();
    source.init();

    return source;
  }

  private GitConfigurationSource getSourceForRemoteRepoWithBranchResolver(BranchResolver branchResolver) {
    GitConfigurationSource source = getSourceBuilderForRemoteRepoWithDefaults()
        .withBranchResolver(branchResolver)
        .build();

    source.init();

    return source;
  }

  private GitConfigurationSource getSourceForRemoteRepoWithPathResolver(PathResolver pathResolver) {
    GitConfigurationSource source = getSourceBuilderForRemoteRepoWithDefaults()
        .withPathResolver(pathResolver)
        .build();

    source.init();

    return source;
  }

  private GitConfigurationSource getSourceForRemoteRepoWithFilesProvider(ConfigFilesProvider configFilesProvider) {
    GitConfigurationSource source = getSourceBuilderForRemoteRepoWithDefaults()
        .withConfigFilesProvider(configFilesProvider)
        .build();

    source.init();

    return source;
  }

  private GitConfigurationSourceBuilder getSourceBuilderForRemoteRepoWithDefaults() {
    return new GitConfigurationSourceBuilder()
        .withRepositoryURI(remoteRepo.dirPath.toString());
  }

  private GitConfigurationSource getSourceForRemoteRepoWithBlankCredentials() {
    GitConfigurationSource source =  new GitConfigurationSourceBuilder()
        .withRepositoryURI(remoteRepo.dirPath.toString())
        .withCredentialsProvider(new UsernamePasswordCredentialsProvider("", ""))
        .build();

    source.init();

    return source;
  }
}