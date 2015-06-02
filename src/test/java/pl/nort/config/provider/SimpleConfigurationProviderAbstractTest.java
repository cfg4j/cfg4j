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
package pl.nort.config.provider;

import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import pl.nort.config.source.ConfigurationSource;

import java.util.Properties;

@RunWith(MockitoJUnitRunner.class)
public abstract class SimpleConfigurationProviderAbstractTest {

  @Rule
  public ExpectedException expectedException = ExpectedException.none();

  protected SimpleConfigurationProvider simpleConfigurationProvider;

  @Mock
  protected ConfigurationSource configurationSource;

  @Before
  public void setUp() throws Exception {
    simpleConfigurationProvider = new SimpleConfigurationProvider(configurationSource);
  }

  protected Properties propertiesWith(String... args) {
    Properties properties = new Properties();
    for (int i = 1; i < args.length; i += 2) {
      properties.put(args[i - 1], args[i]);
    }

    return properties;
  }
}