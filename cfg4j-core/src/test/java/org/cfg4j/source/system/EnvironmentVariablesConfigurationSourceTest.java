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

package org.cfg4j.source.system;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import org.cfg4j.source.context.environment.DefaultEnvironment;
import org.cfg4j.source.context.environment.Environment;
import org.cfg4j.source.context.environment.ImmutableEnvironment;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@RunWith(PowerMockRunner.class)
@PrepareForTest({EnvironmentVariablesConfigurationSource.class})
public class EnvironmentVariablesConfigurationSourceTest {

  @Rule
  public ExpectedException expectedException = ExpectedException.none();

  private EnvironmentVariablesConfigurationSource source;

  @Before
  public void setUp() throws Exception {
    source = new EnvironmentVariablesConfigurationSource();
    source.init();
  }

  @Test
  public void shouldReturnPath() throws Exception {
    assertThat(source.getConfiguration(new DefaultEnvironment())).containsKey("PATH");
  }

  @Test
  public void shouldReturnPathForAnyEnvironment() throws Exception {
    assertThat(source.getConfiguration(mock(Environment.class))).containsKey("PATH");
  }

  @Test
  public void shouldReturnAllVariablesInNamespace() throws Exception {
    // Given
    EnvironmentVariablesConfigurationSource mockSource = new EnvironmentVariablesConfigurationSource();
    PowerMockito.mockStatic(System.class);

    final String namespace = "APPLICATION_NAME";
    Environment nameSpaced = new ImmutableEnvironment(namespace);
    Environment nameSpaceTrailingUnderscore = new ImmutableEnvironment(namespace + "_");

    Map<String, String> mockEnv = new HashMap<String, String>() {{
      put("PATH", "/usr/bin");
      put(namespace + "_PROFILE", "PROD");
      put(namespace + "_USER", "TEST");
    }};

    // When
    Mockito.when(System.getenv()).thenReturn(mockEnv);
    mockSource.init();

    // Then
    Properties config = mockSource.getConfiguration(nameSpaced);
    assertThat(config.containsKey("PROFILE"));
    assertThat(config.containsKey("USER"));
    assertThat(!config.containsKey("PATH"));

    // Then
    Properties configUnderscoreEnv = mockSource.getConfiguration(nameSpaceTrailingUnderscore);
    assertThat(configUnderscoreEnv.containsKey("PROFILE"));
    assertThat(configUnderscoreEnv.containsKey("USER"));
    assertThat(!configUnderscoreEnv.containsKey("PATH"));
  }
}