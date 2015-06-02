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
public class SimpleConfigurationProviderBindTest extends SimpleConfigurationProviderAbstractTest {

  interface ConfigPojo {
    Integer someSetting();

    List<Boolean> otherSetting();
  }

  @Test
  public void bindShouldThrowWhenFetchingNonexistentKey() throws Exception {
    when(configurationSource.getConfiguration()).thenReturn(new Properties());

    expectedException.expect(NoSuchElementException.class);
    simpleConfigurationProvider.bind("", ConfigPojo.class);
  }

  @Test
  public void bindShouldThrowWhenUnableToFetchKey() throws Exception {
    when(configurationSource.getConfiguration()).thenThrow(IllegalStateException.class);

    expectedException.expect(IllegalStateException.class);
    simpleConfigurationProvider.bind("", ConfigPojo.class);
  }

  @Test
  public void bindShouldThrowOnIncompatibleConversion() throws Exception {
    when(configurationSource.getConfiguration()).thenReturn(propertiesWith("someSetting", "42", "otherSetting", "42"));

    expectedException.expect(IllegalArgumentException.class);
    simpleConfigurationProvider.bind("", ConfigPojo.class);
  }

  @Test
  public void shouldBindInitialValues() throws Exception {
    when(configurationSource.getConfiguration()).thenReturn(propertiesWith("someSetting", "42", "otherSetting", "true,false"));

    ConfigPojo config = simpleConfigurationProvider.bind("", ConfigPojo.class);
    assertThat(config.someSetting()).isEqualTo(42);
    assertThat(config.otherSetting()).containsExactly(true, false);
  }

  @Test
  public void shouldBindInitialValuesInSubPath() throws Exception {
    when(configurationSource.getConfiguration()).thenReturn(propertiesWith("myContext.someSetting", "42", "myContext.otherSetting", "true,false"));

    ConfigPojo config = simpleConfigurationProvider.bind("myContext", ConfigPojo.class);
    assertThat(config.someSetting()).isEqualTo(42);
    assertThat(config.otherSetting()).containsExactly(true, false);
  }

  @Test
  public void shouldUpdateValuesAfterChange() throws Exception {
    when(configurationSource.getConfiguration()).thenReturn(propertiesWith("someSetting", "42", "otherSetting", "true,false"));
    ConfigPojo config = simpleConfigurationProvider.bind("", ConfigPojo.class);

    when(configurationSource.getConfiguration()).thenReturn(propertiesWith("someSetting", "0", "otherSetting", "true"));

    assertThat(config.someSetting()).isEqualTo(0);
    assertThat(config.otherSetting()).containsExactly(true);
  }
}
