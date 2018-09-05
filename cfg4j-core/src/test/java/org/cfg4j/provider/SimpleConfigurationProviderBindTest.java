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
package org.cfg4j.provider;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Properties;


class SimpleConfigurationProviderBindTest extends SimpleConfigurationProviderAbstractTest {

  public interface ConfigPojo {
    Integer someSetting();
  }

  public interface MultiPropertyConfigPojo extends ConfigPojo {
    List<Boolean> otherSetting();
  }

  @Test
  void bindThrowsWhenFetchingNonexistentKey() {
    when(configurationSource.getConfiguration(anyEnvironment())).thenReturn(new Properties());

    assertThatThrownBy(() -> simpleConfigurationProvider.bind("", ConfigPojo.class)).isExactlyInstanceOf(NoSuchElementException.class);
  }

  @Test
  void bindThrowsWhenUnableToFetchKey() {
    when(configurationSource.getConfiguration(anyEnvironment())).thenThrow(IllegalStateException.class);

    assertThatThrownBy(() -> simpleConfigurationProvider.bind("", ConfigPojo.class)).isExactlyInstanceOf(IllegalStateException.class);
  }

  @Test
  void bindThrowsOnIncompatibleConversion() {
    when(configurationSource.getConfiguration(anyEnvironment())).thenReturn(propertiesWith("someSetting", "shouldBeNumber"));

    assertThatThrownBy(() -> simpleConfigurationProvider.bind("", ConfigPojo.class)).isExactlyInstanceOf(IllegalArgumentException.class);
  }

  @Test
  void bindsAllInterfaceMethods() {
    when(configurationSource.getConfiguration(anyEnvironment())).thenReturn(propertiesWith("someSetting", "42", "otherSetting", "true,false"));

    MultiPropertyConfigPojo config = simpleConfigurationProvider.bind("", MultiPropertyConfigPojo.class);
    assertThat(config.someSetting()).isEqualTo(42);
    assertThat(config.otherSetting()).containsExactly(true, false);
  }

  @Test
  void bindsInitialValues() {
    when(configurationSource.getConfiguration(anyEnvironment())).thenReturn(propertiesWith("someSetting", "42"));

    ConfigPojo config = simpleConfigurationProvider.bind("", ConfigPojo.class);
    assertThat(config.someSetting()).isEqualTo(42);
  }

  @Test
  void bindsInitialValuesInSubPath() {
    when(configurationSource.getConfiguration(anyEnvironment())).thenReturn(propertiesWith("myContext.someSetting", "42"));

    ConfigPojo config = simpleConfigurationProvider.bind("myContext", ConfigPojo.class);
    assertThat(config.someSetting()).isEqualTo(42);
  }

  @Test
  void reactsToSourceChanges() {
    when(configurationSource.getConfiguration(anyEnvironment())).thenReturn(propertiesWith("someSetting", "42"));
    ConfigPojo config = simpleConfigurationProvider.bind("", ConfigPojo.class);

    when(configurationSource.getConfiguration(anyEnvironment())).thenReturn(propertiesWith("someSetting", "0"));

    assertThat(config.someSetting()).isEqualTo(0);
  }
}
