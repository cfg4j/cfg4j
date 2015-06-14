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
package org.cfg4j.source.compose;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.assertj.core.data.MapEntry;
import org.cfg4j.source.ConfigurationSource;
import org.cfg4j.source.context.Environment;
import org.cfg4j.source.context.ImmutableEnvironment;
import org.cfg4j.source.context.MissingEnvironmentException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Properties;


@RunWith(MockitoJUnitRunner.class)
public class FallbackConfigurationSourceTest {

  private static final int NUMBER_OF_SOURCES = 5;
  private static final int LAST_SOURCE_INDEX = NUMBER_OF_SOURCES - 1;

  @Rule
  public ExpectedException expectedException = ExpectedException.none();

  private ConfigurationSource[] underlyingSources;
  private FallbackConfigurationSource fallbackConfigurationSource;

  @Before
  public void setUp() throws Exception {
    underlyingSources = new ConfigurationSource[5];
    for (int i = 0; i < underlyingSources.length; i++) {
      underlyingSources[i] = mock(ConfigurationSource.class);
    }

    fallbackConfigurationSource = new FallbackConfigurationSource(underlyingSources);
  }

  @Test
  public void getConfigurationShouldThrowWhenAllSourcesThrow() throws Exception {
    makeAllSourcesThrow(IllegalStateException.class);

    expectedException.expect(IllegalStateException.class);
    fallbackConfigurationSource.getConfiguration(new ImmutableEnvironment("test"));
  }

  @Test
  public void getConfigurationShouldSelectFirstAvailableConfiguration() throws Exception {
    makeAllSourcesThrow(IllegalStateException.class);
    underlyingSources[LAST_SOURCE_INDEX] = mock(ConfigurationSource.class);
    when(underlyingSources[LAST_SOURCE_INDEX].getConfiguration()).thenReturn(getProps("prop1", "value1")[0]);

    assertThat(fallbackConfigurationSource.getConfiguration()).containsOnly(MapEntry.entry("prop1", "value1"));
  }

  @Test
  public void getConfiguration2ShouldThrowWhenAllSourcesThrowOnMissingEnvironment() throws Exception {
    makeAllSourcesThrow(MissingEnvironmentException.class);

    expectedException.expect(MissingEnvironmentException.class);
    fallbackConfigurationSource.getConfiguration(mock(Environment.class));
  }

  @Test
  public void getConfiguration2ShouldThrowWhenAllSourcesThrow() throws Exception {
    makeAllSourcesThrow(IllegalStateException.class);

    expectedException.expect(IllegalStateException.class);
    fallbackConfigurationSource.getConfiguration(mock(Environment.class));
  }

  @Test
  public void getConfiguration2ShouldSelectFirstAvailableConfiguration() throws Exception {
    makeAllSourcesThrow(IllegalStateException.class);
    underlyingSources[LAST_SOURCE_INDEX] = mock(ConfigurationSource.class);
    when(underlyingSources[LAST_SOURCE_INDEX].getConfiguration(any(Environment.class))).thenReturn(getProps("prop1", "value1")[0]);

    assertThat(fallbackConfigurationSource.getConfiguration(mock(Environment.class)))
        .containsOnly(MapEntry.entry("prop1", "value1"));
  }

  @Test
  public void refreshShouldTryToRefreshAllSources() throws Exception {
    fallbackConfigurationSource.refresh();

    for (ConfigurationSource underlyingSource : underlyingSources) {
      verify(underlyingSource, atLeastOnce()).refresh();
    }
  }

  @Test
  public void refreshShouldThrowWhenAllSourcesThrow() throws Exception {
    makeAllSourcesThrow(IllegalStateException.class);

    expectedException.expect(IllegalStateException.class);
    fallbackConfigurationSource.refresh();
  }

  @Test
  public void refreshShouldIgnoreExceptionsIfAtLeastOneSourceSucceeds() throws Exception {
    makeAllSourcesThrow(IllegalStateException.class);
    doNothing().when(underlyingSources[LAST_SOURCE_INDEX]).refresh();

    fallbackConfigurationSource.refresh();
  }

  private void makeAllSourcesThrow(Class<? extends Throwable> exceptionClass) {
    for (ConfigurationSource underlyingSource : underlyingSources) {
      when(underlyingSource.getConfiguration()).thenThrow(exceptionClass);
      when(underlyingSource.getConfiguration(any(Environment.class))).thenThrow(exceptionClass);
      doThrow(exceptionClass).when(underlyingSource).refresh();
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