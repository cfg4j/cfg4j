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
package org.cfg4j.source.files;

import static org.assertj.core.api.Assertions.assertThat;

import org.assertj.core.data.MapEntry;
import org.cfg4j.source.context.environment.DefaultEnvironment;
import org.cfg4j.source.context.environment.Environment;
import org.cfg4j.source.context.environment.ImmutableEnvironment;
import org.cfg4j.source.context.environment.MissingEnvironmentException;
import org.cfg4j.source.context.filesprovider.ConfigFilesProvider;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.Properties;


@RunWith(MockitoJUnitRunner.class)
public class FilesConfigurationSourceTest {

  @Rule
  public ExpectedException expectedException = ExpectedException.none();

  private TempConfigurationFileRepo fileRepo;
  private ConfigFilesProvider configFilesProvider;
  private FilesConfigurationSource source;
  private Environment environment;

  @Before
  public void setUp() throws Exception {
    fileRepo = new TempConfigurationFileRepo("org.cfg4j-test-repo");
    fileRepo.changeProperty(Paths.get("application.properties"), "some.setting", "masterValue");
    fileRepo.changeProperty(Paths.get("otherConfig.properties"), "otherConfig.setting", "masterValue");
    fileRepo.changeProperty(Paths.get("malformed.properties"), "otherConfig.setting", "\\uzzzzz");
    fileRepo.changeProperty(Paths.get("otherApplicationConfigs/application.properties"), "some.setting", "otherAppSetting");

    environment = new ImmutableEnvironment(fileRepo.dirPath.toString());

    source = new FilesConfigurationSource();
    source.init();
  }

  @After
  public void tearDown() throws Exception {
    fileRepo.remove();
  }

  @Test
  public void getConfigurationReadsFromDefaultFile() throws Exception {
    assertThat(source.getConfiguration(environment)).containsOnly(MapEntry.entry("some.setting", "masterValue"));
  }

  @Test
  public void getConfigurationReadsFromHomeForDefaultEnvironment() throws Exception {
    System.setProperty("user.home", fileRepo.dirPath.resolve("otherApplicationConfigs").toString());
    assertThat(source.getConfiguration(new DefaultEnvironment())).containsOnly(MapEntry.entry("some.setting", "otherAppSetting"));
  }

  @Test
  public void getConfigurationReadsFromGivenPath() throws Exception {
    Environment environment = new ImmutableEnvironment(fileRepo.dirPath.resolve("otherApplicationConfigs").toString());

    assertThat(source.getConfiguration(environment)).containsOnly(MapEntry.entry("some.setting", "otherAppSetting"));
  }

  @Test
  public void getConfigurationReadsFromGivenFiles() throws Exception {
    configFilesProvider = new ConfigFilesProvider() {
      @Override
      public Iterable<Path> getConfigFiles() {
        return Arrays.asList(
            Paths.get("application.properties"),
            Paths.get("otherConfig.properties")
        );
      }
    };

    source = new FilesConfigurationSource(configFilesProvider);
    assertThat(source.getConfiguration(environment)).containsOnlyKeys("some.setting", "otherConfig.setting");
  }

  @Test
  public void getConfigurationThrowsOnMissingEnvironment() throws Exception {
    expectedException.expect(MissingEnvironmentException.class);
    source.getConfiguration(new ImmutableEnvironment("awlerijawoetinawwerlkjn"));
  }

  @Test
  public void getConfigurationThrowsOnMissingConfigFile() throws Exception {
    fileRepo.deleteFile(Paths.get("application.properties"));

    expectedException.expect(IllegalStateException.class);
    source.getConfiguration(environment);
  }

  @Test
  public void getConfigurationThrowsOnMalformedConfigFile() throws Exception {
    configFilesProvider = new ConfigFilesProvider() {
      @Override
      public Iterable<Path> getConfigFiles() {
        return Collections.singletonList(
            Paths.get("malformed.properties")
        );
      }
    };

    source = new FilesConfigurationSource(configFilesProvider);

    expectedException.expect(IllegalStateException.class);
    source.getConfiguration(environment);
  }
  
  @Test
  public void getConfigurationInoreMissingFiles(){
    configFilesProvider = new ConfigFilesProvider() {
      @Override
      public Iterable<Path> getConfigFiles() {
        return Collections.singletonList(
            Paths.get("malformed.properties")
        );
      }
    };

    source = new FilesConfigurationSource(configFilesProvider);
    source.setIgnoreNonExistingFiles(true);

    expectedException.expect(IllegalStateException.class);
    Properties properties = source.getConfiguration(environment);
    assertThat(properties).isEmpty();
  }

}