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
import org.cfg4j.source.SourceCommunicationException;
import org.cfg4j.source.context.environment.Environment;
import org.cfg4j.source.context.environment.MissingEnvironmentException;
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
    fallbackConfigurationSource.init();
  }

  @Test
  public void getConfigurationShouldThrowWhenAllSourcesThrowOnMissingEnvironment() throws Exception {
    makeAllSourcesThrow(new MissingEnvironmentException(""));

    expectedException.expect(MissingEnvironmentException.class);
    fallbackConfigurationSource.getConfiguration(mock(Environment.class));
  }

  @Test
  public void getConfigurationShouldThrowWhenAllSourcesThrow() throws Exception {
    makeAllSourcesThrow(new IllegalStateException());

    expectedException.expect(IllegalStateException.class);
    fallbackConfigurationSource.getConfiguration(mock(Environment.class));
  }

  @Test
  public void getConfigurationShouldSelectFirstAvailableConfiguration() throws Exception {
    makeAllSourcesThrow(new IllegalStateException());
    underlyingSources[LAST_SOURCE_INDEX] = mock(ConfigurationSource.class);
    when(underlyingSources[LAST_SOURCE_INDEX].getConfiguration(any(Environment.class))).thenReturn(getProps("prop1", "value1")[0]);

    assertThat(fallbackConfigurationSource.getConfiguration(mock(Environment.class)))
        .containsOnly(MapEntry.entry("prop1", "value1"));
  }

  @Test
  public void initShouldTryToInitializeAllSources() throws Exception {
    for (ConfigurationSource underlyingSource : underlyingSources) {
      verify(underlyingSource, atLeastOnce()).init();
    }
  }

  @Test
  public void initShouldThrowWhenAllSourcesThrow() throws Exception {
    makeAllSourcesThrow(new IllegalStateException());

    expectedException.expect(IllegalStateException.class);
    fallbackConfigurationSource.init();
  }

  @Test
  public void initShouldIgnoreIllegalStateExceptionsIfAtLeastOneSourceSucceeds() throws Exception {
    makeAllSourcesThrow(new IllegalStateException());
    doNothing().when(underlyingSources[LAST_SOURCE_INDEX]).init();

    fallbackConfigurationSource.init();
  }

  @Test
  public void initShouldIgnoreSourceCommunicationExceptionsIfAtLeastOneSourceSucceeds() throws Exception {
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