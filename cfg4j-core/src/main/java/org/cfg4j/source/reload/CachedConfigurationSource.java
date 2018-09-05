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

import org.cfg4j.source.ConfigurationSource;
import org.cfg4j.source.context.environment.Environment;
import org.cfg4j.source.context.environment.MissingEnvironmentException;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * A {@link ConfigurationSource} that caches configuration between calls to the {@link #reload(Environment)} method.
 */
public class CachedConfigurationSource implements ConfigurationSource {

  private final Map<String, Properties> cachedConfigurationPerEnvironment;
  private final ConfigurationSource underlyingSource;

  /**
   * Create a new cached configuration source backed by {@code underlyingSource}.
   *
   * @param underlyingSource source used to load data into cache.
   */
  public CachedConfigurationSource(ConfigurationSource underlyingSource) {
    this.underlyingSource = requireNonNull(underlyingSource);

    cachedConfigurationPerEnvironment = new HashMap<>();
  }

  /**
   * Get configuration set for a given {@code environment} from the cache. For cache to be seeded
   * you have to call the {@link #reload(Environment)} method before calling this method. Otherwise
   * the method will throw {@link MissingEnvironmentException}.
   *
   * @param environment environment to use
   * @return configuration set for {@code environment}
   * @throws MissingEnvironmentException when there's no config for the given environment in the cache
   */
  @Override
  public Properties getConfiguration(Environment environment) {
    if (cachedConfigurationPerEnvironment.containsKey(environment.getName())) {
      return cachedConfigurationPerEnvironment.get(environment.getName());
    } else {
      throw new MissingEnvironmentException(environment.getName());
    }
  }

  @Override
  public void init() {
    underlyingSource.init();
  }

  /**
   * Reload configuration set for a given {@code environment} from this source in a form of {@link Properties}.
   * After reload completes the configuration can be accesses via {@link #getConfiguration(Environment)} method.
   *
   * @param environment environment to reload
   * @throws MissingEnvironmentException when requested environment couldn't be found
   * @throws IllegalStateException       when unable to fetch configuration
   */
  public void reload(Environment environment) {
    Properties configuration = underlyingSource.getConfiguration(environment);
    cachedConfigurationPerEnvironment.put(environment.getName(), configuration);
  }
}
