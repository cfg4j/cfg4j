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
package org.cfg4j.source;

import org.cfg4j.source.context.environment.Environment;
import org.cfg4j.source.context.environment.MissingEnvironmentException;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public abstract class BaseConfigurationSource implements ConfigurationSource {

  private final Map<Environment, Properties> cachedConfigurationPerEnvironment;

  public BaseConfigurationSource() {
    cachedConfigurationPerEnvironment = new HashMap<>();
  }

  @Override
  public Properties getConfiguration(Environment environment) {
    return cachedConfigurationPerEnvironment.get(environment);
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
  @Override
  public void reload(Environment environment) {
    cachedConfigurationPerEnvironment.put(environment, fetchConfiguration(environment));
  }

  protected abstract Properties fetchConfiguration(Environment environment);
}
