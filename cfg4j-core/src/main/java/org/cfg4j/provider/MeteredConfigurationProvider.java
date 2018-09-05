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

import static java.util.Objects.requireNonNull;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;

import java.util.Properties;

/**
 * Decorator for {@link SimpleConfigurationProvider} that emits execution metrics. It emits the following metrics (each of those prefixed
 * with a string passed at construction time):
 * <ul>
 * <li>allConfigurationAsProperties</li>
 * <li>getProperty</li>
 * <li>getPropertyGeneric</li>
 * <li>bind</li>
 * </ul>
 * Each of those metrics is of {@link Timer} type (i.e. includes execution time percentiles, execution count, etc.)
 */
class MeteredConfigurationProvider implements ConfigurationProvider {

  private final SimpleConfigurationProvider delegate;

  private final Timer allConfigurationAsPropertiesTimer;
  private final Timer getPropertyTimer;
  private final Timer getPropertyGenericTimer;
  private final Timer bindTimer;

  /**
   * Create decorator for given {@code delegate} and using {@code metricRegistry} for constructing metrics. Each metric will
   * be prefixed with {@code metricPrefix}
   *
   * @param metricRegistry metric registry to hold execution metrics
   * @param metricPrefix   prefix for metric names (trailing dot will be added to it)
   * @param delegate       configuration provider to monitor
   */
  MeteredConfigurationProvider(MetricRegistry metricRegistry, String metricPrefix, SimpleConfigurationProvider delegate) {
    requireNonNull(metricRegistry);
    requireNonNull(metricPrefix);
    this.delegate = requireNonNull(delegate);

    allConfigurationAsPropertiesTimer = metricRegistry.timer(metricPrefix + "allConfigurationAsProperties");
    getPropertyTimer = metricRegistry.timer(metricPrefix + "getProperty");
    getPropertyGenericTimer = metricRegistry.timer(metricPrefix + "getPropertyGeneric");
    bindTimer = metricRegistry.timer(metricPrefix + "bind");
  }

  @Override
  public Properties allConfigurationAsProperties() {
    Timer.Context context = allConfigurationAsPropertiesTimer.time();

    try {
      return delegate.allConfigurationAsProperties();
    } finally {
      context.stop();
    }
  }

  @Override
  public <T> T getProperty(String key, Class<T> type) {
    Timer.Context context = getPropertyTimer.time();

    try {
      return delegate.getProperty(key, type);
    } finally {
      context.stop();
    }
  }

  @Override
  public <T> T getProperty(String key, GenericTypeInterface genericType) {
    Timer.Context context = getPropertyGenericTimer.time();

    try {
      return delegate.getProperty(key, genericType);
    } finally {
      context.stop();
    }
  }

  @Override
  public <T> T bind(String prefix, Class<T> type) {
    Timer.Context context = bindTimer.time();

    try {
      return delegate.bind(this, prefix, type);
    } finally {
      context.stop();
    }
  }
}
