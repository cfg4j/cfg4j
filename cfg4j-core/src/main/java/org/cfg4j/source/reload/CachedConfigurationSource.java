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

  private final Map<Environment, Properties> cachedConfigurationPerEnvironment;
  private final ConfigurationSource underlyingSource;

  public CachedConfigurationSource(ConfigurationSource underlyingSource) {
    this.underlyingSource = requireNonNull(underlyingSource);

    cachedConfigurationPerEnvironment = new HashMap<>();
  }

  @Override
  public Properties getConfiguration(Environment environment) {
    return cachedConfigurationPerEnvironment.get(environment);
  }

  @Override
  public void init() {
    underlyingSource.init();
  }

  /**
   * Reload configuration set for a given {@code environment} from this source in a form of {@link Properties}.
   * After reload completes the configuration can be accesses via {@link #getConfiguration(Environment)} method.
   * Any source extending this class is responsible for mapping an {@link Environment} to the internal data representation.
   * Please document the resolution mechanism in the class javadoc.
   *
   * @param environment environment to reload
   * @throws MissingEnvironmentException when requested environment couldn't be found
   * @throws IllegalStateException       when unable to fetch configuration
   */
  public void reload(Environment environment) {
    Properties configuration = underlyingSource.getConfiguration(environment);
    cachedConfigurationPerEnvironment.put(environment, configuration);
  }
}
