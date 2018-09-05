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
package org.cfg4j.source.compose;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.assertj.core.data.MapEntry;
import org.cfg4j.source.ConfigurationSource;
import org.cfg4j.source.SourceCommunicationException;
import org.cfg4j.source.context.environment.Environment;
import org.cfg4j.source.context.environment.MissingEnvironmentException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Properties;


class FallbackConfigurationSourceTest {

  private static final int NUMBER_OF_SOURCES = 5;
  private static final int LAST_SOURCE_INDEX = NUMBER_OF_SOURCES - 1;


  private ConfigurationSource[] underlyingSources;
  private FallbackConfigurationSource fallbackConfigurationSource;

  @BeforeEach
  void setUp() {
    underlyingSources = new ConfigurationSource[5];
    for (int i = 0; i < underlyingSources.length; i++) {
      underlyingSources[i] = mock(ConfigurationSource.class);
    }

    fallbackConfigurationSource = new FallbackConfigurationSource(underlyingSources);
    fallbackConfigurationSource.init();
  }

  @Test
  void getConfigurationThrowsWhenAllSourcesThrowOnMissingEnvironment() {
    makeAllSourcesThrow(new MissingEnvironmentException(""));

    assertThatThrownBy(() -> fallbackConfigurationSource.getConfiguration(mock(Environment.class))).isExactlyInstanceOf(MissingEnvironmentException.class);
  }

  @Test
  void getConfigurationThrowsWhenAllSourcesThrow() {
    makeAllSourcesThrow(new IllegalStateException());

    assertThatThrownBy(() -> fallbackConfigurationSource.getConfiguration(mock(Environment.class))).isExactlyInstanceOf(IllegalStateException.class);
  }

  @Test
  void getConfigurationSelectsFirstAvailableConfiguration() {
    makeAllSourcesThrow(new IllegalStateException());
    underlyingSources[LAST_SOURCE_INDEX] = mock(ConfigurationSource.class);
    when(underlyingSources[LAST_SOURCE_INDEX].getConfiguration(any(Environment.class))).thenReturn(getProps("prop1", "value1")[0]);

    assertThat(fallbackConfigurationSource.getConfiguration(mock(Environment.class)))
        .containsOnly(MapEntry.entry("prop1", "value1"));
  }

  @Test
  void initInitializesAllSources() {
    for (ConfigurationSource underlyingSource : underlyingSources) {
      verify(underlyingSource, atLeastOnce()).init();
    }
  }

  @Test
  void initThrowsWhenAllSourcesThrow() {
    makeAllSourcesThrow(new IllegalStateException());

    assertThatThrownBy(() -> fallbackConfigurationSource.init()).isExactlyInstanceOf(IllegalStateException.class);
  }

  @Test
  void initIgnoresIllegalStateExceptionsIfAtLeastOneSourceSucceeds() {
    makeAllSourcesThrow(new IllegalStateException());
    doNothing().when(underlyingSources[LAST_SOURCE_INDEX]).init();

    fallbackConfigurationSource.init();
  }

  @Test
  void initIgnoresSourceCommunicationExceptionsIfAtLeastOneSourceSucceeds() {
    makeAllSourcesThrow(new SourceCommunicationException("", null));
    doNothing().when(underlyingSources[LAST_SOURCE_INDEX]).init();

    fallbackConfigurationSource.init();
  }

  private void makeAllSourcesThrow(Throwable exception) {
    for (ConfigurationSource underlyingSource : underlyingSources) {
      when(underlyingSource.getConfiguration(any(Environment.class))).thenThrow(exception);
      doThrow(exception).when(underlyingSource).init();
    }
  }

  private Properties[] getProps(String... props) {
    Properties[] properties = new Properties[props.length / 2];
    for (int i = 1; i < props.length; i += 2) {
      properties[i / 2] = new Properties();
      properties[i / 2].put(props[i - 1], props[i]);
    }

    return properties;
  }
}