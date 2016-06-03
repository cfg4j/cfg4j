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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.assertj.core.data.MapEntry;
import org.cfg4j.source.ConfigurationSource;
import org.cfg4j.source.context.environment.Environment;
import org.cfg4j.source.context.environment.ImmutableEnvironment;
import org.cfg4j.source.context.environment.MissingEnvironmentException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Properties;


@RunWith(MockitoJUnitRunner.class)
public class MergeConfigurationSourceTest {

  @Rule
  public ExpectedException expectedException = ExpectedException.none();

  private ConfigurationSource[] underlyingSources;
  private MergeConfigurationSource mergeConfigurationSource;

  @Before
  public void setUp() throws Exception {
    underlyingSources = new ConfigurationSource[5];
    for (int i = 0; i < underlyingSources.length; i++) {
      underlyingSources[i] = mock(ConfigurationSource.class);
      when(underlyingSources[i].getConfiguration(any(Environment.class))).thenReturn(new Properties());
    }

    mergeConfigurationSource = new MergeConfigurationSource(underlyingSources);
    mergeConfigurationSource.init();
  }

  @Test
  public void getConfigurationShouldThrowWhenOneOfSourcesThrowsOnMissingEnvironment() throws Exception {
    when(underlyingSources[1].getConfiguration(Matchers.<Environment>any())).thenThrow(new MissingEnvironmentException(""));

    expectedException.expect(MissingEnvironmentException.class);
    mergeConfigurationSource.getConfiguration(new ImmutableEnvironment("test"));
  }

  @Test
  public void getConfigurationShouldThrowWhenOneOfSourcesThrows() throws Exception {
    when(underlyingSources[3].getConfiguration(Matchers.<Environment>any())).thenThrow(new IllegalStateException());

    expectedException.expect(IllegalStateException.class);
    mergeConfigurationSource.getConfiguration(new ImmutableEnvironment("test"));
  }

  @Test
  public void getConfigurationShouldMergeConfigurations() throws Exception {
    Environment environment = new ImmutableEnvironment("test");

    sourcesWithProps(environment, "prop1", "value1", "prop2", "value2");

    assertThat(mergeConfigurationSource.getConfiguration(environment)).containsOnly(MapEntry.entry("prop1", "value1"),
        MapEntry.entry("prop2", "value2"));
  }

  @Test
  public void getConfigurationShouldMergeConfigurationsWithCollidingKeys() throws Exception {
    Environment environment = new ImmutableEnvironment("test");

    sourcesWithProps(environment, "prop", "value1", "prop", "value2");

    assertThat(mergeConfigurationSource.getConfiguration(environment)).containsOnly(MapEntry.entry("prop", "value2"));
  }

  @Test
  public void initShouldInitializeAllSources() throws Exception {
    for (ConfigurationSource underlyingSource : underlyingSources) {
      verify(underlyingSource, atLeastOnce()).init();
    }
  }

  private void sourcesWithProps(Environment environment, String... props) {
    Properties[] properties = getProps(props);

    for (int i = 0; i < properties.length; i++) {
      when(underlyingSources[i].getConfiguration(environment)).thenReturn(properties[i]);
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