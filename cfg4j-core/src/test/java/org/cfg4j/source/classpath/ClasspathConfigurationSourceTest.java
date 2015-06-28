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
package org.cfg4j.source.classpath;

import static org.assertj.core.api.Assertions.assertThat;

import org.assertj.core.data.MapEntry;
import org.cfg4j.source.ConfigFilesProvider;
import org.cfg4j.source.context.DefaultEnvironment;
import org.cfg4j.source.context.Environment;
import org.cfg4j.source.context.ImmutableEnvironment;
import org.cfg4j.source.context.MissingEnvironmentException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.util.Arrays;
import java.util.Collections;


@RunWith(MockitoJUnitRunner.class)
public class ClasspathConfigurationSourceTest {

  @Rule
  public ExpectedException expectedException = ExpectedException.none();

  private TempConfigurationClasspathRepo classpathRepo;
  private ConfigFilesProvider configFilesProvider;
  private ClasspathConfigurationSource source;
  private FileSystem fileSystem;

  @Before
  public void setUp() throws Exception {
    classpathRepo = new TempConfigurationClasspathRepo();
    fileSystem = FileSystems.getDefault();

    configFilesProvider = () -> Collections.singletonList(
        fileSystem.getPath("application.properties")
    );

    source = new ClasspathConfigurationSource(configFilesProvider);
  }

  @Test
  public void getConfiguration2ShouldReadFromGivenPath() throws Exception {
    configFilesProvider = () -> Collections.singletonList(
        fileSystem.getPath("application.properties")
    );

    source = new ClasspathConfigurationSource(configFilesProvider);

    Environment environment = new ImmutableEnvironment("/otherApplicationConfigs/");

    assertThat(source.getConfiguration(environment)).containsOnly(MapEntry.entry("some.setting", "otherAppSetting"));
  }

  @Test
  public void getConfiguration2ShouldReadFromGivenFiles() throws Exception {
    configFilesProvider = () -> Arrays.asList(
        fileSystem.getPath("application.properties"),
        fileSystem.getPath("otherConfig.properties")
    );

    source = new ClasspathConfigurationSource(configFilesProvider);
    assertThat(source.getConfiguration(new DefaultEnvironment())).containsOnlyKeys("some.setting", "otherConfig.setting");
  }

  @Test
  public void getConfiguration2ShouldThrowOnMissingEnvironment() throws Exception {
    expectedException.expect(MissingEnvironmentException.class);
    source.getConfiguration(new ImmutableEnvironment("awlerijawoetinawwerlkjn"));
  }

  @Test
  public void getConfiguration2ShouldThrowOnMissingConfigFile() throws Exception {
    configFilesProvider = () -> Collections.singletonList(
        fileSystem.getPath("nonexistent.properties")
    );

    source = new ClasspathConfigurationSource(configFilesProvider);

    expectedException.expect(IllegalStateException.class);
    source.getConfiguration(new DefaultEnvironment());
  }

  @Test
  public void getConfiguration2ShouldThrowOnMalformedConfigFile() throws Exception {
    configFilesProvider = () -> Collections.singletonList(
        fileSystem.getPath("malformed.properties")
    );

    source = new ClasspathConfigurationSource(configFilesProvider);

    expectedException.expect(IllegalStateException.class);
    source.getConfiguration(new DefaultEnvironment());
  }

  @Test
  public void reloadShouldUpdateGetConfiguration2OnDefaultBranch() throws Exception {
    classpathRepo.changeProperty("application.properties", "some.setting", "changedValue");
    source.reload();

    assertThat(source.getConfiguration(new DefaultEnvironment())).containsOnly(MapEntry.entry("some.setting", "changedValue"));
  }
}