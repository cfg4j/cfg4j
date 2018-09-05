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
package org.cfg4j.provider;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.cfg4j.source.context.environment.ImmutableEnvironment;
import org.cfg4j.source.context.environment.MissingEnvironmentException;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Properties;


class SimpleConfigurationProviderGetPropertyTest extends SimpleConfigurationProviderAbstractTest {

  @Test
  void allConfigurationAsPropertiesThrowsWhenUnableToFetchConfiguration() {
    when(configurationSource.getConfiguration(anyEnvironment())).thenThrow(new IllegalStateException());

    // FIXME: expectedException.expect(IllegalStateException.class);
    simpleConfigurationProvider.allConfigurationAsProperties();
  }

  @Test
  void allConfigurationAsPropertiesThrowsWhenMissingEnvironment() {
    when(configurationSource.getConfiguration(anyEnvironment())).thenThrow(new MissingEnvironmentException(""));

    // FIXME: expectedException.expect(IllegalStateException.class);
    simpleConfigurationProvider.allConfigurationAsProperties();
  }

  @Test
  void allConfigurationAsPropertiesUsesProvidedEnvironment() {
    when(configurationSource.getConfiguration(anyEnvironment())).thenReturn(new Properties());

    simpleConfigurationProvider.allConfigurationAsProperties();

    verify(configurationSource).getConfiguration(environment);
  }

  @Test
  void getProperty2ThrowsWhenFetchingNonexistentKey() {
    when(configurationSource.getConfiguration(anyEnvironment())).thenReturn(new Properties());

    // FIXME: expectedException.expect(NoSuchElementException.class);
    simpleConfigurationProvider.getProperty("some.property", String.class);
  }

  @Test
  void getProperty2ThrowsWhenUnableToFetchKey() {
    when(configurationSource.getConfiguration(anyEnvironment())).thenThrow(new IllegalStateException());

    // FIXME: expectedException.expect(IllegalStateException.class);
    simpleConfigurationProvider.getProperty("some.property", String.class);
  }

  @Test
  void getProperty2ThrowsOnIncompatibleConversion() {
    when(configurationSource.getConfiguration(anyEnvironment())).thenReturn(propertiesWith("some.property", "true"));

    // FIXME: expectedException.expect(IllegalArgumentException.class);
    simpleConfigurationProvider.getProperty("some.property", Integer.class);
  }

  @Test
  void getProperty2ReturnsPropertyFromSource() {
    when(configurationSource.getConfiguration(anyEnvironment())).thenReturn(propertiesWith("some.property", "true"));

    Boolean property = simpleConfigurationProvider.getProperty("some.property", Boolean.class);
    assertThat(property).isTrue();
  }

  @Test
  void getProperty2ReactsToSourceChanges() {
    when(configurationSource.getConfiguration(anyEnvironment())).thenReturn(propertiesWith("some.property", "true"));
    when(configurationSource.getConfiguration(anyEnvironment())).thenReturn(propertiesWith("some.property", "false"));

    Boolean property = simpleConfigurationProvider.getProperty("some.property", Boolean.class);
    assertThat(property).isFalse();
  }

  @Test
  void getProperty2ReturnsArrayPropertyFromSource() {
    when(configurationSource.getConfiguration(anyEnvironment())).thenReturn(propertiesWith("some.property", "42.5, 99.9999"));

    double[] property = simpleConfigurationProvider.getProperty("some.property", double[].class);
    assertThat(property).containsExactly(42.5, 99.9999);
  }

  @Test
  void getProperty3ThrowsWhenFetchingNonexistentKey() {
    when(configurationSource.getConfiguration(anyEnvironment())).thenReturn(new Properties());

    // FIXME: expectedException.expect(NoSuchElementException.class);
    simpleConfigurationProvider.getProperty("some.property", new GenericType<List<String>>() {
    });
  }

  @Test
  void getProperty3ThrowsWhenUnableToFetchKey() {
    when(configurationSource.getConfiguration(anyEnvironment())).thenThrow(new IllegalStateException());

    // FIXME: expectedException.expect(IllegalStateException.class);
    simpleConfigurationProvider.getProperty("some.property", new GenericType<List<String>>() {
    });
  }

  @Test
  void getProperty3ThrowsOnIncompatibleConversion() {
    when(configurationSource.getConfiguration(anyEnvironment())).thenReturn(propertiesWith("some.property", "true"));

    // FIXME: expectedException.expect(IllegalArgumentException.class);
    simpleConfigurationProvider.getProperty("some.property", new GenericType<List<Integer>>() {
    });
  }

  @Test
  void getProperty3ReturnsPropertyFromSource() {
    when(configurationSource.getConfiguration(anyEnvironment())).thenReturn(propertiesWith("some.property", "1,2"));

    List<Integer> properties = simpleConfigurationProvider.getProperty("some.property", new GenericType<List<Integer>>() {
    });
    assertThat(properties).containsExactly(1, 2);
  }

  @Test
  void getProperty3ReactsToSourceChanges() {
    when(configurationSource.getConfiguration(anyEnvironment())).thenReturn(propertiesWith("some.property", "1,2"));
    when(configurationSource.getConfiguration(anyEnvironment())).thenReturn(propertiesWith("some.property", "3,4,5"));

    List<Integer> properties = simpleConfigurationProvider.getProperty("some.property", new GenericType<List<Integer>>() {
    });
    assertThat(properties).containsExactly(3,4,5);
  }

  @Test
  void getPropertyReturnsPropertyForProperEnvironment() {
    when(configurationSource.getConfiguration(environment)).thenReturn(propertiesWith("some.property", "1"));
    when(configurationSource.getConfiguration(new ImmutableEnvironment("test_env"))).thenReturn(propertiesWith("some.property", "2"));

    Integer property = simpleConfigurationProvider.getProperty("some.property", Integer.class);

    assertThat(property).isEqualTo(1);
  }
}
