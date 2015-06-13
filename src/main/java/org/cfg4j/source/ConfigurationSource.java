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
package org.cfg4j.source;

import org.cfg4j.source.context.Environment;
import org.cfg4j.source.context.MissingEnvironmentException;
import org.cfg4j.source.refresh.Refreshable;

import java.util.Properties;

/**
 * Provides access to configuration store and exposes configuration values in bulk {@link Properties} format.
 */
public interface ConfigurationSource extends Refreshable {

  /**
   * Get configuration set from this source in a form of {@link Properties}. Uses default environment.
   *
   * @return configuration set for default environment
   * @throws IllegalStateException when unable to fetch configuration
   */
  @Deprecated
  Properties getConfiguration();

  /**
   * Get configuration set for a given {@code environment} from this source in a form of {@link Properties}.
   * Provided {@link Environment} will be used to determine which environment to use.
   *
   * @param environment environment to use
   * @return configuration set for {@code environment}
   * @throws MissingEnvironmentException when requested environment couldn't be found
   * @throws IllegalStateException       when unable to fetch configuration
   */
  Properties getConfiguration(Environment environment);
}
