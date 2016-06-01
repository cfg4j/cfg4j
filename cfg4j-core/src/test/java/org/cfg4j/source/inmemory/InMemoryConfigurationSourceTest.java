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

package org.cfg4j.source.inmemory;

import static org.assertj.core.api.Assertions.assertThat;

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

}