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
import static org.junit.jupiter.api.Assumptions.assumeTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import org.cfg4j.source.context.environment.DefaultEnvironment;
import org.cfg4j.source.context.environment.Environment;
import org.cfg4j.source.context.environment.ImmutableEnvironment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIf;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;


class EnvironmentVariablesConfigurationSourceTest {



  private EnvironmentVariablesConfigurationSource source;

  @BeforeEach
  void setUp() throws Exception {
    source = new EnvironmentVariablesConfigurationSource();
    source.init();
  }

  @Test
  void returnsPath() throws Exception {
    assertThat(source.getConfiguration(new DefaultEnvironment())).containsKey("PATH");
  }

  @Test
  void returnsPathForAnyEnvironment() throws Exception {
    assertThat(source.getConfiguration(mock(Environment.class))).containsKey("PATH");
  }

  @Test
  @EnabledIf("systemProperty.get('os.arch') == null")
  void returnsAllVariablesInNamespace() throws Exception {
    // Given
    EnvironmentVariablesConfigurationSource mockSource = new EnvironmentVariablesConfigurationSource();

    final String namespace = "APPLICATION_NAME";
    Environment nameSpaced = new ImmutableEnvironment(namespace);
    Environment nameSpaceTrailingUnderscore = new ImmutableEnvironment(namespace + "_");

    Map<String, String> mockEnv = new HashMap<>() {{
      put("PATH", "/usr/bin");
      put(namespace + "_PROFILE", "PROD");
      put(namespace + "_USER", "TEST");
    }};

    System mock = mock(System.class);
    given(mock.getenv()).willReturn(mockEnv);

    System.setProperty(namespace + "_PROFILE", "PROD");
    System.setProperty(namespace + "_USER", "TEST");
    assumeTrue("CI".equals(System.getenv("ENV")));


    // When
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