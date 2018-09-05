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

package org.cfg4j.source.metered;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Properties;


@ExtendWith(MockitoExtension.class)
class MeteredConfigurationSourceTest {

  @Mock
  private ConfigurationSource delegate;

  @Mock
  private MetricRegistry metricRegistry;

  private MeteredConfigurationSource source;

  @BeforeEach
  void setUp() {
    Timer timer = mock(Timer.class);
    when(timer.time()).thenReturn(mock(Timer.Context.class));
    when(metricRegistry.timer(anyString())).thenReturn(timer);

    source = new MeteredConfigurationSource(metricRegistry, "configSource", delegate);
    source.init();
  }

  @Test
  void getConfigurationCallsDelegate() {
    Properties properties = new Properties();
    when(delegate.getConfiguration(any(Environment.class))).thenReturn(properties);

    assertThat(source.getConfiguration(new DefaultEnvironment())).isEqualTo(properties);
  }

  @Test
  void getConfigurationPropagatesMissingEnvironmentExceptions() {
    when(delegate.getConfiguration(any(Environment.class))).thenThrow(new MissingEnvironmentException(""));

    assertThatThrownBy(() -> source.getConfiguration(new DefaultEnvironment())).isExactlyInstanceOf(MissingEnvironmentException.class);
  }

  @Test
  void getConfigurationPropagatesIllegalStateExceptions() {
    when(delegate.getConfiguration(any(Environment.class))).thenThrow(new IllegalStateException(""));

    assertThatThrownBy(() -> source.getConfiguration(new DefaultEnvironment())).isExactlyInstanceOf(IllegalStateException.class);
  }

  @Test
  void initCallsDelegate() {
    verify(delegate, times(1)).init();
  }

  @Test
  void initPropagatesIllegalStateExceptions() {
    doThrow(new IllegalStateException("")).when(delegate).init();

    assertThatThrownBy(() -> delegate.init()).isExactlyInstanceOf(IllegalStateException.class);
  }

  @Test
  void initPropagatesSourceCommunicationExceptions() {
    doThrow(new SourceCommunicationException("", null)).when(delegate).init();

    assertThatThrownBy(() -> delegate.init()).isExactlyInstanceOf(SourceCommunicationException.class);
  }
}