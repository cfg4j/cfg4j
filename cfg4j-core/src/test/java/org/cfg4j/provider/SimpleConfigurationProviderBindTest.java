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
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Properties;

@RunWith(MockitoJUnitRunner.class)
public class SimpleConfigurationProviderBindTest extends ConfigurationProviderAbstractTest {

  public interface ConfigPojo {
    Integer someSetting();
  }

  public interface MultiPropertyConfigPojo extends ConfigPojo {
    List<Boolean> otherSetting();
  }

  @Test
  public void bindShouldThrowWhenFetchingNonexistentKey() throws Exception {
    when(configurationSource.getConfiguration(anyEnvironment())).thenReturn(new Properties());

    expectedException.expect(NoSuchElementException.class);
    configurationProvider.bind("", ConfigPojo.class);
  }

  @Test
  public void bindShouldThrowWhenUnableToFetchKey() throws Exception {
    when(configurationSource.getConfiguration(anyEnvironment())).thenThrow(IllegalStateException.class);

    expectedException.expect(IllegalStateException.class);
    configurationProvider.bind("", ConfigPojo.class);
  }

  @Test
  public void bindShouldThrowOnIncompatibleConversion() throws Exception {
    when(configurationSource.getConfiguration(anyEnvironment())).thenReturn(propertiesWith("someSetting", "shouldBeNumber"));

    expectedException.expect(IllegalArgumentException.class);
    configurationProvider.bind("", ConfigPojo.class);
  }

  @Test
  public void shouldBindAllInterfaceMethods() throws Exception {
    when(configurationSource.getConfiguration(anyEnvironment())).thenReturn(propertiesWith("someSetting", "42", "otherSetting", "true,false"));

    MultiPropertyConfigPojo config = configurationProvider.bind("", MultiPropertyConfigPojo.class);
    assertThat(config.someSetting()).isEqualTo(42);
    assertThat(config.otherSetting()).containsExactly(true, false);
  }

  @Test
  public void shouldBindInitialValues() throws Exception {
    when(configurationSource.getConfiguration(anyEnvironment())).thenReturn(propertiesWith("someSetting", "42"));

    ConfigPojo config = configurationProvider.bind("", ConfigPojo.class);
    assertThat(config.someSetting()).isEqualTo(42);
  }

  @Test
  public void shouldBindInitialValuesInSubPath() throws Exception {
    when(configurationSource.getConfiguration(anyEnvironment())).thenReturn(propertiesWith("myContext.someSetting", "42"));

    ConfigPojo config = configurationProvider.bind("myContext", ConfigPojo.class);
    assertThat(config.someSetting()).isEqualTo(42);
  }

  @Test
  public void shouldReactToSourceChanges() throws Exception {
    when(configurationSource.getConfiguration(anyEnvironment())).thenReturn(propertiesWith("someSetting", "42"));
    ConfigPojo config = configurationProvider.bind("", ConfigPojo.class);

    when(configurationSource.getConfiguration(anyEnvironment())).thenReturn(propertiesWith("someSetting", "0"));

    assertThat(config.someSetting()).isEqualTo(0);
  }

  public interface NestedConfigPojo {
    Integer someSetting();
    ConfigPojo nestedPojo();
  }

  public interface UltraNestedConfigPojo {
    Integer someSetting();
    NestedConfigPojo nestedPojo();
  }

  @Test
  public void shouldBindInitialValuesInSubPathToNestedObjects() throws Exception {
    when(configurationSource.getConfiguration(anyEnvironment())).thenReturn(propertiesWith("myContext.nestedPojo.nestedPojo.someSetting", "1",
      "myContext.nestedPojo.someSetting", "2",
      "myContext.someSetting", "3"));
    UltraNestedConfigPojo config = configurationProvider.bind("myContext", UltraNestedConfigPojo.class);
    assertThat(config.someSetting()).isEqualTo(3);
    assertThat(config.nestedPojo().someSetting()).isEqualTo(2);
    assertThat(config.nestedPojo().nestedPojo().someSetting()).isEqualTo(1);
  }


  public interface MapPojo {
    Map<String, Integer> map();
  }

  @Test
  public void shouldBindInitalValuesInSubPathToMap() throws Exception {
    when(configurationSource.getConfiguration(anyEnvironment())).thenReturn(propertiesWith(
      "myContext.map.a", "1",
      "myContext.map.b", "2",
      "myContext.map.c", "3"));
    MapPojo config = configurationProvider.bind("myContext", MapPojo.class);
    assertThat(config.map().get("a")).isEqualTo(1);
    assertThat(config.map().get("b")).isEqualTo(2);
    assertThat(config.map().get("c")).isEqualTo(3);
  }
}
