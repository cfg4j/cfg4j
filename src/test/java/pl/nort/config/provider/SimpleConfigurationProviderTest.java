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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import pl.nort.config.source.ConfigurationSource;

import java.util.NoSuchElementException;
import java.util.Properties;

@RunWith(MockitoJUnitRunner.class)
public class SimpleConfigurationProviderTest {

  @Rule
  public ExpectedException expectedException = ExpectedException.none();

  private SimpleConfigurationProvider simpleConfigurationProvider;

  @Mock
  private ConfigurationSource configurationSource;

  @Before
  public void setUp() throws Exception {
    simpleConfigurationProvider = new SimpleConfigurationProvider(configurationSource);
  }

  @Test
  public void allConfigurationAsPropertiesShouldThrowWhenUnableToFetchConfiguration() throws Exception {
    when(configurationSource.getConfiguration()).thenThrow(IllegalStateException.class);

    expectedException.expect(IllegalStateException.class);
    simpleConfigurationProvider.allConfigurationAsProperties();
  }

  @Test
  public void getPropertyShouldThrowWhenFetchingNonexistentKey() throws Exception {
    when(configurationSource.getConfiguration()).thenReturn(new Properties());

    expectedException.expect(NoSuchElementException.class);
    simpleConfigurationProvider.getProperty("some.property");
  }

  @Test
  public void getPropertyShouldThrowWhenUnableToFetchKey() throws Exception {
    when(configurationSource.getConfiguration()).thenThrow(IllegalStateException.class);

    expectedException.expect(IllegalStateException.class);
    simpleConfigurationProvider.getProperty("some.property");
  }

  @Test
  public void getPropertyShouldReturnStringPropertyFromSource() throws Exception {
    when(configurationSource.getConfiguration()).thenReturn(propertiesWith("some.property", "abc"));

    String property = simpleConfigurationProvider.getProperty("some.property");
    assertThat(property).isEqualTo("abc");
  }

  @Test
  public void getProperty2ShouldThrowWhenFetchingNonexistentKey() throws Exception {
    when(configurationSource.getConfiguration()).thenReturn(new Properties());

    expectedException.expect(NoSuchElementException.class);
    simpleConfigurationProvider.getProperty("some.property", String.class);
  }

  @Test
  public void getProperty2ShouldThrowWhenUnableToFetchKey() throws Exception {
    when(configurationSource.getConfiguration()).thenThrow(IllegalStateException.class);

    expectedException.expect(IllegalStateException.class);
    simpleConfigurationProvider.getProperty("some.property", String.class);
  }

  @Test
  public void getProperty2ShouldReturnPropertyFromSource() throws Exception {
    when(configurationSource.getConfiguration()).thenReturn(propertiesWith("some.property", "true"));

    Boolean property = simpleConfigurationProvider.getProperty("some.property", Boolean.class);
    assertThat(property).isTrue();
  }

  private Properties propertiesWith(String... args) {
    Properties properties = new Properties();
    properties.put(args[0], args[1]);

    return properties;
  }
}