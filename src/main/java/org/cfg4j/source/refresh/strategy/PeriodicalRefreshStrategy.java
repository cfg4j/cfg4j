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
package org.cfg4j.source.refresh.strategy;

import static java.util.Objects.requireNonNull;

import org.cfg4j.source.refresh.RefreshStrategy;
import org.cfg4j.source.refresh.Refreshable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

/**
 * {@link RefreshStrategy} that refreshes the resource periodically. It spawns a background tread!
 */
public class PeriodicalRefreshStrategy implements RefreshStrategy {

  private static final Logger LOG = LoggerFactory.getLogger(PeriodicalRefreshStrategy.class);

  private final long duration;
  private final TimeUnit timeUnit;
  private final Timer timer;

  /**
   * <b>Deprecated: use {@link #PeriodicalRefreshStrategy(long, TimeUnit)} instead.</b>
   *
   * Construct strategy that refreshes the resource every {@code refreshAfterMs} ms.
   * First refresh will happen immediately after calling {@link #init(Refreshable)}. Each following
   * refresh will happen {@code refreshAfterMs} ms after the previous one completed.
   *
   * @param refreshAfterMs time (in milliseconds) between refreshes
   */
  @Deprecated
  public PeriodicalRefreshStrategy(long refreshAfterMs) {
    this(refreshAfterMs, TimeUnit.MILLISECONDS);
  }

  /**
   * Construct strategy that refreshes the resource every {@code duration} (measured in {@code timeUnit}s).
   * First refresh will happen immediately after calling {@link #init(Refreshable)}. Each following
   * refresh will happen {@code refreshAfterMs} ms after the previous one completed.
   *
   * @param duration time (in {@code timeUnit}) between refreshes
   * @param timeUnit time unit to use
   */
  public PeriodicalRefreshStrategy(long duration, TimeUnit timeUnit) {
    this.duration = duration;
    this.timeUnit = requireNonNull(timeUnit);
    timer = new Timer();
  }

  @Override
  public void init(Refreshable resource) {
    LOG.info("Initializing " + PeriodicalRefreshStrategy.class + "with refresh time of " + duration + timeUnit);

    resource.refresh();

    timer.schedule(new TimerTask() {
      @Override
      public void run() {
        resource.refresh();
      }
    }, timeUnit.toMillis(duration), timeUnit.toMillis(duration));
  }

  @Override
  public void shutdown() {
    LOG.info("Shutting down " + PeriodicalRefreshStrategy.class);
    timer.cancel();
  }
}
