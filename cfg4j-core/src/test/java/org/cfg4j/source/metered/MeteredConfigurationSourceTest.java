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

package org.cfg4j.source.metered;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import org.cfg4j.source.ConfigurationSource;
import org.cfg4j.source.SourceCommunicationException;
import org.cfg4j.source.context.environment.DefaultEnvironment;
import org.cfg4j.source.context.environment.Environment;
import org.cfg4j.source.context.environment.MissingEnvironmentException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Properties;


@RunWith(MockitoJUnitRunner.class)
public class MeteredConfigurationSourceTest {

  @Rule
  public ExpectedException expectedException = ExpectedException.none();

  @Mock
  private ConfigurationSource delegate;

  @Mock
  private MetricRegistry metricRegistry;

  private MeteredConfigurationSource source;

  @Before
  public void setUp() throws Exception {
    Timer timer = mock(Timer.class);
    when(timer.time()).thenReturn(mock(Timer.Context.class));
    when(metricRegistry.timer(anyString())).thenReturn(timer);

    source = new MeteredConfigurationSource(metricRegistry, "configSource", delegate);
    source.init();
  }

  @Test
  public void getConfigurationShouldCallDelegate() throws Exception {
    Properties properties = new Properties();
    when(delegate.getConfiguration(any(Environment.class))).thenReturn(properties);

    assertThat(source.getConfiguration(new DefaultEnvironment())).isEqualTo(properties);
  }

  @Test
  public void getConfigurationShouldPropagateMissingEnvironmentExceptions() throws Exception {
    when(delegate.getConfiguration(any(Environment.class))).thenThrow(new MissingEnvironmentException(""));

    expectedException.expect(MissingEnvironmentException.class);
    source.getConfiguration(new DefaultEnvironment());
  }

  @Test
  public void getConfigurationShouldPropagateIllegalStateExceptions() throws Exception {
    when(delegate.getConfiguration(any(Environment.class))).thenThrow(new IllegalStateException(""));

    expectedException.expect(IllegalStateException.class);
    source.getConfiguration(new DefaultEnvironment());
  }

  @Test
  public void initShouldCallDelegate() throws Exception {
    verify(delegate, times(1)).init();
  }

  @Test
  public void initShouldPropagateIllegalStateExceptions() throws Exception {
    doThrow(new IllegalStateException("")).when(delegate).init();

    expectedException.expect(IllegalStateException.class);
    delegate.init();
  }

  @Test
  public void initShouldPropagateSourceCommunicationExceptions() throws Exception {
    doThrow(new SourceCommunicationException("", null)).when(delegate).init();

    expectedException.expect(SourceCommunicationException.class);
    delegate.init();
  }
}