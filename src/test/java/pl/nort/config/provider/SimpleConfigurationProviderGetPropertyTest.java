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

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Properties;

@RunWith(MockitoJUnitRunner.class)
public class SimpleConfigurationProviderGetPropertyTest extends SimpleConfigurationProviderAbstractTest {

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
  public void getPropertyShouldReactToSourceChanges() throws Exception {
    when(configurationSource.getConfiguration()).thenReturn(propertiesWith("some.property", "abc"));
    when(configurationSource.getConfiguration()).thenReturn(propertiesWith("some.property", "cde"));

    String property = simpleConfigurationProvider.getProperty("some.property");
    assertThat(property).isEqualTo("cde");
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
  public void getProperty2ShouldThrowOnIncompatibleConversion() throws Exception {
    when(configurationSource.getConfiguration()).thenReturn(propertiesWith("some.property", "true"));

    expectedException.expect(IllegalArgumentException.class);
    simpleConfigurationProvider.getProperty("some.property", Integer.class);
  }

  @Test
  public void getProperty2ShouldReturnPropertyFromSource() throws Exception {
    when(configurationSource.getConfiguration()).thenReturn(propertiesWith("some.property", "true"));

    Boolean property = simpleConfigurationProvider.getProperty("some.property", Boolean.class);
    assertThat(property).isTrue();
  }

  @Test
  public void getProperty2ShouldReactToSourceChanges() throws Exception {
    when(configurationSource.getConfiguration()).thenReturn(propertiesWith("some.property", "true"));
    when(configurationSource.getConfiguration()).thenReturn(propertiesWith("some.property", "false"));

    Boolean property = simpleConfigurationProvider.getProperty("some.property", Boolean.class);
    assertThat(property).isFalse();
  }

  @Test
  public void getProperty2ShouldReturnArrayPropertyFromSource() throws Exception {
    when(configurationSource.getConfiguration()).thenReturn(propertiesWith("some.property", "42.5, 99.9999"));

    double[] property = simpleConfigurationProvider.getProperty("some.property", double[].class);
    assertThat(property).containsExactly(42.5, 99.9999);
  }

  @Test
  public void getProperty3ShouldThrowWhenFetchingNonexistentKey() throws Exception {
    when(configurationSource.getConfiguration()).thenReturn(new Properties());

    expectedException.expect(NoSuchElementException.class);
    simpleConfigurationProvider.getProperty("some.property", new GenericType<List<String>>() {
    });
  }

  @Test
  public void getProperty3ShouldThrowWhenUnableToFetchKey() throws Exception {
    when(configurationSource.getConfiguration()).thenThrow(IllegalStateException.class);

    expectedException.expect(IllegalStateException.class);
    simpleConfigurationProvider.getProperty("some.property", new GenericType<List<String>>() {
    });
  }

  @Test
  public void getProperty3ShouldThrowOnIncompatibleConversion() throws Exception {
    when(configurationSource.getConfiguration()).thenReturn(propertiesWith("some.property", "true"));

    expectedException.expect(IllegalArgumentException.class);
    simpleConfigurationProvider.getProperty("some.property", new GenericType<List<Integer>>() {
    });
  }

  @Test
  public void getProperty3ShouldReturnPropertyFromSource() throws Exception {
    when(configurationSource.getConfiguration()).thenReturn(propertiesWith("some.property", "1,2"));

    List<Integer> properties = simpleConfigurationProvider.getProperty("some.property", new GenericType<List<Integer>>() {
    });
    assertThat(properties).containsExactly(1, 2);
  }

  @Test
  public void getProperty3ShouldReactToSourceChanges() throws Exception {
    when(configurationSource.getConfiguration()).thenReturn(propertiesWith("some.property", "1,2"));
    when(configurationSource.getConfiguration()).thenReturn(propertiesWith("some.property", "3,4,5"));

    List<Integer> properties = simpleConfigurationProvider.getProperty("some.property", new GenericType<List<Integer>>() {
    });
    assertThat(properties).containsExactly(3,4,5);
  }

}
