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
import static org.cfg4j.utils.PropertiesUtils.propertiesWith;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.cfg4j.source.context.environment.ImmutableEnvironment;
import org.cfg4j.source.context.environment.MissingEnvironmentException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Properties;

@RunWith(MockitoJUnitRunner.class)
public class SimpleConfigurationProviderGetPropertyTest extends ConfigurationProviderAbstractTest {

  @Test
  public void allConfigurationAsPropertiesShouldThrowWhenUnableToFetchConfiguration() throws Exception {
    when(configurationSource.getConfiguration(anyEnvironment())).thenThrow(new IllegalStateException());

    expectedException.expect(IllegalStateException.class);
    configurationProvider.allConfigurationAsProperties();
  }

  @Test
  public void allConfigurationAsPropertiesShouldThrowWhenMissingEnvironment() throws Exception {
    when(configurationSource.getConfiguration(anyEnvironment())).thenThrow(new MissingEnvironmentException(""));

    expectedException.expect(IllegalStateException.class);
    configurationProvider.allConfigurationAsProperties();
  }

  @Test
  public void allConfigurationAsPropertiesShouldUseProvidedEnvironment() throws Exception {
    when(configurationSource.getConfiguration(anyEnvironment())).thenReturn(new Properties());

    configurationProvider.allConfigurationAsProperties();

    verify(configurationSource).getConfiguration(environment);
  }

  @Test
  public void getProperty2ShouldThrowWhenFetchingNonexistentKey() throws Exception {
    when(configurationSource.getConfiguration(anyEnvironment())).thenReturn(new Properties());

    expectedException.expect(NoSuchElementException.class);
    configurationProvider.getProperty("some.property", String.class);
  }

  @Test
  public void getProperty2ShouldThrowWhenUnableToFetchKey() throws Exception {
    when(configurationSource.getConfiguration(anyEnvironment())).thenThrow(new IllegalStateException());

    expectedException.expect(IllegalStateException.class);
    configurationProvider.getProperty("some.property", String.class);
  }

  @Test
  public void getProperty2ShouldThrowOnIncompatibleConversion() throws Exception {
    when(configurationSource.getConfiguration(anyEnvironment())).thenReturn(propertiesWith("some.property", "true"));

    expectedException.expect(IllegalArgumentException.class);
    configurationProvider.getProperty("some.property", Integer.class);
  }

  @Test
  public void getProperty2ShouldReturnPropertyFromSource() throws Exception {
    when(configurationSource.getConfiguration(anyEnvironment())).thenReturn(propertiesWith("some.property", "true"));

    Boolean property = configurationProvider.getProperty("some.property", Boolean.class);
    assertThat(property).isTrue();
  }

  @Test
  public void getProperty2ShouldReactToSourceChanges() throws Exception {
    when(configurationSource.getConfiguration(anyEnvironment())).thenReturn(propertiesWith("some.property", "true"));
    when(configurationSource.getConfiguration(anyEnvironment())).thenReturn(propertiesWith("some.property", "false"));

    Boolean property = configurationProvider.getProperty("some.property", Boolean.class);
    assertThat(property).isFalse();
  }

  @Test
  public void getProperty2ShouldReturnArrayPropertyFromSource() throws Exception {
    when(configurationSource.getConfiguration(anyEnvironment())).thenReturn(propertiesWith("some.property", "42.5, 99.9999"));

    double[] property = configurationProvider.getProperty("some.property", double[].class);
    assertThat(property).containsExactly(42.5, 99.9999);
  }

  @Test
  public void getProperty3ShouldThrowWhenFetchingNonexistentKey() throws Exception {
    when(configurationSource.getConfiguration(anyEnvironment())).thenReturn(new Properties());

    expectedException.expect(NoSuchElementException.class);
    configurationProvider.getProperty("some.property", new GenericType<List<String>>() {
    });
  }

  @Test
  public void getProperty3ShouldThrowWhenUnableToFetchKey() throws Exception {
    when(configurationSource.getConfiguration(anyEnvironment())).thenThrow(new IllegalStateException());

    expectedException.expect(IllegalStateException.class);
    configurationProvider.getProperty("some.property", new GenericType<List<String>>() {
    });
  }

  @Test
  public void getProperty3ShouldThrowOnIncompatibleConversion() throws Exception {
    when(configurationSource.getConfiguration(anyEnvironment())).thenReturn(propertiesWith("some.property", "true"));

    expectedException.expect(IllegalArgumentException.class);
    configurationProvider.getProperty("some.property", new GenericType<List<Integer>>() {
    });
  }

  @Test
  public void getProperty3ShouldReturnPropertyFromSource() throws Exception {
    when(configurationSource.getConfiguration(anyEnvironment())).thenReturn(propertiesWith("some.property", "1,2"));

    List<Integer> properties = configurationProvider.getProperty("some.property", new GenericType<List<Integer>>() {
    });
    assertThat(properties).containsExactly(1, 2);
  }

  @Test
  public void getProperty3ShouldReactToSourceChanges() throws Exception {
    when(configurationSource.getConfiguration(anyEnvironment())).thenReturn(propertiesWith("some.property", "1,2"));
    when(configurationSource.getConfiguration(anyEnvironment())).thenReturn(propertiesWith("some.property", "3,4,5"));

    List<Integer> properties = configurationProvider.getProperty("some.property", new GenericType<List<Integer>>() {
    });
    assertThat(properties).containsExactly(3,4,5);
  }

  @Test
  public void getPropertyShouldReturnPropertyForProperEnvironment() throws Exception {
    when(configurationSource.getConfiguration(environment)).thenReturn(propertiesWith("some.property", "1"));
    when(configurationSource.getConfiguration(new ImmutableEnvironment("test_env"))).thenReturn(propertiesWith("some.property", "2"));

    Integer property = configurationProvider.getProperty("some.property", Integer.class);

    assertThat(property).isEqualTo(1);
  }
}
