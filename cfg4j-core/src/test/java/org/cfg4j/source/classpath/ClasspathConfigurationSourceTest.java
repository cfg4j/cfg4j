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
package org.cfg4j.source.classpath;

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
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;


@ExtendWith(MockitoExtension.class)
class ClasspathConfigurationSourceTest {

  private TempConfigurationClasspathRepo classpathRepo;
  private ConfigFilesProvider configFilesProvider;
  private ClasspathConfigurationSource source;

  @BeforeEach
  void setUp() {
    classpathRepo = new TempConfigurationClasspathRepo();

    source = new ClasspathConfigurationSource();
    source.init();
  }

  @AfterEach
  void tearDown() throws Exception {
    classpathRepo.close();
  }

  @Test
  void getConfigurationReadsFromGivenPath() {
    Environment environment = new ImmutableEnvironment("otherApplicationConfigs");

    assertThat(source.getConfiguration(environment)).containsOnly(MapEntry.entry("some.setting", "otherAppSetting"));
  }

  @Test
  void getConfigurationDisallowsLeadingSlashInClasspathLocation() {
    Environment environment = new ImmutableEnvironment("/otherApplicationConfigs");

    assertThatThrownBy(() -> source.getConfiguration(environment)).isExactlyInstanceOf(MissingEnvironmentException.class);
  }

  @Test
  void getConfigurationReadsFromGivenFiles() {
    configFilesProvider = () -> Arrays.asList(
        Paths.get("application.properties"),
        Paths.get("otherConfig.properties")
    );

    source = new ClasspathConfigurationSource(configFilesProvider);
    assertThat(source.getConfiguration(new DefaultEnvironment())).containsOnlyKeys("some.setting", "otherConfig.setting");
  }

  @Test
  void getConfigurationThrowsOnMissingEnvironment() {
    assertThatThrownBy(() -> source.getConfiguration(new ImmutableEnvironment("awlerijawoetinawwerlkjn"))).isExactlyInstanceOf(MissingEnvironmentException.class);
  }

  @Test
  void getConfigurationThrowsOnMissingConfigFile() {
    configFilesProvider = () -> Collections.singletonList(
        Paths.get("nonexistent.properties")
    );

    source = new ClasspathConfigurationSource(configFilesProvider);

    assertThatThrownBy(() -> source.getConfiguration(new DefaultEnvironment())).isExactlyInstanceOf(IllegalStateException.class);
  }

  @Test
  void getConfigurationThrowsOnMalformedConfigFile() {
    configFilesProvider = () -> Collections.singletonList(
        Paths.get("malformed.properties")
    );

    source = new ClasspathConfigurationSource(configFilesProvider);

    assertThatThrownBy(() -> source.getConfiguration(new DefaultEnvironment())).isExactlyInstanceOf(IllegalStateException.class);
  }

}