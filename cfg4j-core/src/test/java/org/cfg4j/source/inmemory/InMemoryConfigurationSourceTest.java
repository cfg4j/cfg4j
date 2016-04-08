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

package org.cfg4j.source.inmemory;

import static org.assertj.core.api.Assertions.assertThat;

import org.assertj.core.data.MapEntry;
import org.cfg4j.source.context.environment.DefaultEnvironment;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Properties;


@RunWith(MockitoJUnitRunner.class)
public class InMemoryConfigurationSourceTest {

  @Rule
  public ExpectedException expectedException = ExpectedException.none();

  private InMemoryConfigurationSource source;
  private Properties properties;

  @Before
  public void setUp() throws Exception {
    properties = new Properties();
    properties.put("sample.setting", "value");

    source = new InMemoryConfigurationSource(properties);
    source.init();
  }

  @Test
  public void shouldReturnSourceProperties() throws Exception {
    assertThat(source.getConfiguration(new DefaultEnvironment())).isEqualTo(properties);
  }

  @Test
  public void shouldNotReactToChangesToSourceProperties() throws Exception {
    properties.put("other.setting", "hello");

    assertThat(source.getConfiguration(new DefaultEnvironment())).doesNotContain(MapEntry.entry("other.setting", "hello"));
  }

  @Test
  public void getConfigurationShouldNotChangeBetweenReloads() throws Exception {
    Properties configurationBefore = source.getConfiguration(new DefaultEnvironment());
    properties.put("other.setting", "hello Norbert!");
    Properties configurationAfter = source.getConfiguration(new DefaultEnvironment());

    assertThat(configurationBefore).isEqualTo(configurationAfter);
  }

  @Test
  public void reloadShouldReactToChangesToSourceProperties() throws Exception {
    properties.put("other.setting", "hello");
    source.reload();

    assertThat(source.getConfiguration(new DefaultEnvironment())).contains(MapEntry.entry("other.setting", "hello"));
  }

}