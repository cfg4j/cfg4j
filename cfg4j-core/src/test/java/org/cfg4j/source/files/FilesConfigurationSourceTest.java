/*
 * Copyright 2015-2018 Norbert Potocki (norbert.potocki@nort.pl)
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
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.assertj.core.data.MapEntry;
import org.cfg4j.source.context.environment.DefaultEnvironment;
import org.cfg4j.source.context.environment.Environment;
import org.cfg4j.source.context.environment.ImmutableEnvironment;
import org.cfg4j.source.context.environment.MissingEnvironmentException;
import org.cfg4j.source.context.filesprovider.ConfigFilesProvider;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;


class FilesConfigurationSourceTest {

  private TempConfigurationFileRepo fileRepo;
  private ConfigFilesProvider configFilesProvider;
  private FilesConfigurationSource source;
  private Environment environment;

  @BeforeEach
  void setUp() throws Exception {
    fileRepo = new TempConfigurationFileRepo("org.cfg4j-test-repo");
    fileRepo.changeProperty(Paths.get("application.properties"), "some.setting", "masterValue");
    fileRepo.changeProperty(Paths.get("otherConfig.properties"), "otherConfig.setting", "masterValue");
    fileRepo.changeProperty(Paths.get("malformed.properties"), "otherConfig.setting", "\\uzzzzz");
    fileRepo.changeProperty(Paths.get("otherApplicationConfigs/application.properties"), "some.setting", "otherAppSetting");

    environment = new ImmutableEnvironment(fileRepo.dirPath.toString());

    source = new FilesConfigurationSource();
    source.init();
  }

  @AfterEach
  void tearDown() throws Exception {
    fileRepo.remove();
  }

  @Test
  void getConfigurationReadsFromDefaultFile() {
    assertThat(source.getConfiguration(environment)).containsOnly(MapEntry.entry("some.setting", "masterValue"));
  }

  @Test
  void getConfigurationReadsFromHomeForDefaultEnvironment() {
    System.setProperty("user.home", fileRepo.dirPath.resolve("otherApplicationConfigs").toString());
    assertThat(source.getConfiguration(new DefaultEnvironment())).containsOnly(MapEntry.entry("some.setting", "otherAppSetting"));
  }

  @Test
  void getConfigurationReadsFromGivenPath() {
    Environment environment = new ImmutableEnvironment(fileRepo.dirPath.resolve("otherApplicationConfigs").toString());

    assertThat(source.getConfiguration(environment)).containsOnly(MapEntry.entry("some.setting", "otherAppSetting"));
  }

  @Test
  void getConfigurationReadsFromGivenFiles() {
    configFilesProvider = () -> Arrays.asList(
        Paths.get("application.properties"),
        Paths.get("otherConfig.properties")
    );

    source = new FilesConfigurationSource(configFilesProvider);
    assertThat(source.getConfiguration(environment)).containsOnlyKeys("some.setting", "otherConfig.setting");
  }

  @Test
  void getConfigurationThrowsOnMissingEnvironment() {
    assertThatThrownBy(() -> source.getConfiguration(new ImmutableEnvironment("awlerijawoetinawwerlkjn"))).isExactlyInstanceOf(MissingEnvironmentException.class);
  }

  @Test
  void getConfigurationThrowsOnMissingConfigFile() throws Exception {
    fileRepo.deleteFile(Paths.get("application.properties"));

    assertThatThrownBy(() -> source.getConfiguration(environment)).isExactlyInstanceOf(IllegalStateException.class);
  }

  @Test
  void getConfigurationThrowsOnMalformedConfigFile() {
    configFilesProvider = () -> Collections.singletonList(
        Paths.get("malformed.properties")
    );

    source = new FilesConfigurationSource(configFilesProvider);

    assertThatThrownBy(() -> source.getConfiguration(environment)).isExactlyInstanceOf(IllegalStateException.class);
  }

}