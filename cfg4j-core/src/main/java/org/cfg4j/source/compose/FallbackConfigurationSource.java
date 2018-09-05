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
package org.cfg4j.source.compose;

import static java.util.Objects.requireNonNull;

import org.cfg4j.source.ConfigurationSource;
import org.cfg4j.source.SourceCommunicationException;
import org.cfg4j.source.context.environment.Environment;
import org.cfg4j.source.context.environment.MissingEnvironmentException;

import java.util.Arrays;
import java.util.Properties;

/**
 * Combines multiple {@link ConfigurationSource}s in a fallback chain. When one of the sources is not available
 * another one is used for providing configuration.
 */
public class FallbackConfigurationSource implements ConfigurationSource {

  private final ConfigurationSource[] sources;

  /**
   * Create a fallback chain of {@link ConfigurationSource}s
   *
   * @param sources configuration sources to use
   */
  public FallbackConfigurationSource(ConfigurationSource... sources) {
    this.sources = requireNonNull(sources);

    for (ConfigurationSource source : sources) {
      requireNonNull(source);
    }
  }

  /**
   * Get configuration set for a given {@code environment} from this source in a form of {@link Properties}.
   * The configuration set is a result of the first {@link ConfigurationSource#getConfiguration(Environment)}
   * call to underlying sources that succeeds. Sources are called in a provided order.
   *
   * @param environment environment to use
   * @return configuration set for {@code environment} from the first source that works
   * @throws MissingEnvironmentException when requested environment couldn't be found in any of the underlying source
   * @throws IllegalStateException       when unable to fetch configuration from any of the underlying sources
   */
  @Override
  public Properties getConfiguration(Environment environment) {

    boolean allMissEnvironment = true;

    for (ConfigurationSource source : sources) {
      try {
        return source.getConfiguration(environment);
      } catch (MissingEnvironmentException e) {
        // NOP
      } catch (IllegalStateException e) {
        allMissEnvironment = false;
      }
    }

    if (allMissEnvironment) {
      throw new MissingEnvironmentException(environment.getName());
    }

    throw new IllegalStateException();
  }

  @Override
  public void init() {
    boolean atLeastOneSuccess = false;

    for (ConfigurationSource source : sources) {
      try {
        source.init();
        atLeastOneSuccess = true;
      } catch (IllegalStateException | SourceCommunicationException e) {
        // NOP
      }
    }

    if (!atLeastOneSuccess) {
      throw new IllegalStateException("Unable to initialize any of the underlying sources");
    }
  }

  @Override
  public String toString() {
    return "FallbackConfigurationSource{" +
        "sources=" + Arrays.toString(sources) +
        '}';
  }
}
