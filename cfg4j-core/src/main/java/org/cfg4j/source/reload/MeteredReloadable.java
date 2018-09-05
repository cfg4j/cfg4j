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

package org.cfg4j.source.reload;

import static java.util.Objects.requireNonNull;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;

/**
 * Decorator for {@link Reloadable} that emits execution metrics. It emits the following metrics (each of those prefixed
 * with a string passed at construction time):
 * <ul>
 * <li>reloadable.reload</li>
 * </ul>
 * Each of those metrics is of {@link Timer} type (i.e. includes execution time percentiles, execution count, etc.)
 */
public class MeteredReloadable implements Reloadable {

  private final Reloadable delegate;
  private final Timer reloadTimer;

  /**
   * Create decorator for given {@code delegate} and using {@code metricRegistry} for constructing metrics. Each metric will
   * be prefixed with {@code metricPrefix}.
   *
   * @param metricRegistry metric registry to hold execution metrics
   * @param metricPrefix   prefix for metric names (trailing dot will be added to it)
   * @param delegate       configuration provider to monitor
   */
  public MeteredReloadable(MetricRegistry metricRegistry, String metricPrefix, Reloadable delegate) {
    requireNonNull(metricRegistry);
    requireNonNull(metricPrefix);
    this.delegate = requireNonNull(delegate);

    reloadTimer = metricRegistry.timer(metricPrefix + "reloadable.reload");
  }

  @Override
  public void reload() {
    Timer.Context context = reloadTimer.time();

    try {
      delegate.reload();
    } finally {
      context.stop();
    }
  }
}
