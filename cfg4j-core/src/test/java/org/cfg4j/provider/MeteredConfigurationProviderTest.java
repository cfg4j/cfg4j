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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

@ExtendWith(MockitoExtension.class)
class MeteredConfigurationProviderTest {

  private interface ConfigPojo {
  }

  @Mock
  private SimpleConfigurationProvider delegate;

  @Mock
  private MetricRegistry metricRegistry;

  private MeteredConfigurationProvider provider;

  @BeforeEach
  void setUp() {
    Timer timer = mock(Timer.class);
    when(timer.time()).thenReturn(mock(Timer.Context.class));
    when(metricRegistry.timer(anyString())).thenReturn(timer);

    provider = new MeteredConfigurationProvider(metricRegistry, "configProvider", delegate);
  }

  @Test
  void allConfigurationAsPropertiesCallsDelegate() {
    Properties properties = new Properties();
    when(delegate.allConfigurationAsProperties()).thenReturn(properties);

    assertThat(provider.allConfigurationAsProperties()).isEqualTo(properties);
  }

  @Test
  void getPropertyCallsDelegate() {
    when(delegate.getProperty("test.property", boolean.class)).thenReturn(true);

    assertThat(provider.getProperty("test.property", boolean.class)).isTrue();
  }

  @Test
  void getProperty2CallsDelegate() {
    @SuppressWarnings("Convert2Diamond")
    GenericType<List<String>> genericType = new GenericType<List<String>>() {
    };
    when(delegate.getProperty(eq("test.property"), eq(genericType))).thenReturn(new LinkedList<>());

    List<String> property = provider.getProperty("test.property", genericType);
    assertThat(property).isEmpty();
  }

  @Test
  void bindCallsDelegate() {
    ConfigPojo configPojo = new ConfigPojo() {
    };
    when(delegate.bind(any(ConfigurationProvider.class), eq(""), eq(ConfigPojo.class))).thenReturn(configPojo);

    assertThat(provider.bind("", ConfigPojo.class)).isEqualTo(configPojo);
  }
}