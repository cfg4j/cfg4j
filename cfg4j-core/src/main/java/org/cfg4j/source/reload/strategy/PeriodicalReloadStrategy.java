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
package org.cfg4j.source.reload.strategy;

import static java.util.Objects.requireNonNull;

import org.cfg4j.source.reload.ReloadStrategy;
import org.cfg4j.source.reload.Reloadable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

/**
 * {@link ReloadStrategy} that reloads resources periodically. Supports multiple resources. It spawns a tread.
 */
public class PeriodicalReloadStrategy implements ReloadStrategy {

  private static final Logger LOG = LoggerFactory.getLogger(PeriodicalReloadStrategy.class);

  private final long duration;
  private final TimeUnit timeUnit;
  private final Timer timer;
  private final Map<Reloadable, TimerTask> tasks;

  /**
   * Construct strategy that reloads the resource every {@code duration} (measured in {@code timeUnit}s).
   * First reload will happen immediately after calling {@link #register(Reloadable)}. Each following
   * reload will happen {@code duration} (measured in {@code timeUnit}s) after the previous one completed
   * until the resource is deregistered with a call to {@link #deregister(Reloadable)} method. Supports
   * multiple resources.
   *
   * @param duration time (in {@code timeUnit}) between reloads
   * @param timeUnit time unit to use
   */
  public PeriodicalReloadStrategy(long duration, TimeUnit timeUnit) {
    this.duration = duration;
    this.timeUnit = requireNonNull(timeUnit);
    tasks = Collections.synchronizedMap(new HashMap<>());
    timer = new Timer();
  }

  @Override
  public void register(final Reloadable resource) {
    LOG.debug("Registering resource " + resource
        + " with reload time of " + duration + " " + timeUnit.toString().toLowerCase());

    TimerTask timerTask = new TimerTask() {
      @Override
      public void run() {
        try {
          resource.reload();
        } catch (Exception e) {
          LOG.warn("Periodical resource reload failed. Will re-try at the next scheduled time.", e);
        }
      }
    };

    timerTask.run();

    tasks.put(resource, timerTask);
    timer.schedule(timerTask, timeUnit.toMillis(duration), timeUnit.toMillis(duration));
  }

  @Override
  public void deregister(Reloadable resource) {
    LOG.debug("De-registering resource " + resource);

    TimerTask timerTask = tasks.remove(resource);
    if (timerTask != null) {
      timerTask.cancel();
    }
  }

  @Override
  public String toString() {
    return "PeriodicalReloadStrategy{" +
        "duration=" + duration +
        ", timeUnit=" + timeUnit +
        ", timer=" + timer +
        '}';
  }
}
