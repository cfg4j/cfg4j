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

package org.cfg4j.source.system;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import org.cfg4j.source.context.environment.DefaultEnvironment;
import org.cfg4j.source.context.environment.Environment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


class SystemPropertiesConfigurationSourceTest {




  private SystemPropertiesConfigurationSource source;

  @BeforeEach
  void setUp() {
    source = new SystemPropertiesConfigurationSource();
    source.init();
  }

  @Test
  void returnsOSName() {
    assertThat(source.getConfiguration(new DefaultEnvironment())).containsKey("os.name");
  }

  @Test
  void returnsOsNameForAnyEnvironment() {
    assertThat(source.getConfiguration(mock(Environment.class))).containsKey("os.name");
  }

  @Test
  void doesNotReturnUndefinedKeys() {
    assertThat(source.getConfiguration(new DefaultEnvironment())).doesNotContainKey("missing.property");
  }

  @Test
  void returnsManuallyDefinedKeys() {
    System.setProperty("manually.added.property", "defined");
    assertThat(source.getConfiguration(new DefaultEnvironment())).containsKey("manually.added.property");
  }

  @Test
  void returnsManuallyDefinedKeysAfterReload() {
    assertThat(source.getConfiguration(new DefaultEnvironment())).doesNotContainKey("reloaded.property");
    System.setProperty("reloaded.property", "defined");
    assertThat(source.getConfiguration(new DefaultEnvironment())).containsKey("reloaded.property");
  }

}