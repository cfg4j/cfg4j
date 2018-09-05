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
package org.cfg4j.source;

import org.cfg4j.source.context.environment.Environment;
import org.cfg4j.source.context.environment.MissingEnvironmentException;
import org.cfg4j.source.inmemory.InMemoryConfigurationSource;

import java.util.Properties;

/**
 * Provides access to configuration store and exposes configuration values in bulk {@link Properties} format.
 * See {@link InMemoryConfigurationSource} for a simple implementation of this interface.
 */
public interface ConfigurationSource {

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

  /**
   * Initialize this source. This method has to be called before any other method of this instance.
   *
   * @throws IllegalStateException        when source was improperly configured
   * @throws SourceCommunicationException when unable to communicate with source
   */
  void init();
}
