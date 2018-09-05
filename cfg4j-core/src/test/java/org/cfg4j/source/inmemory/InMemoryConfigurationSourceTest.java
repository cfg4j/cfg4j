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

package org.cfg4j.source.inmemory;

import static org.assertj.core.api.Assertions.assertThat;

import org.cfg4j.source.context.environment.DefaultEnvironment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Properties;


class InMemoryConfigurationSourceTest {




  private InMemoryConfigurationSource source;
  private Properties properties;

  @BeforeEach
  void setUp() {
    properties = new Properties();
    properties.put("sample.setting", "value");

    source = new InMemoryConfigurationSource(properties);
    source.init();
  }

  @Test
  void returnsSourceProperties() {
    assertThat(source.getConfiguration(new DefaultEnvironment())).isEqualTo(properties);
  }

}