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
import org.cfg4j.source.context.environment.Environment;
import org.cfg4j.source.context.environment.MissingEnvironmentException;

import java.util.Arrays;
import java.util.Properties;

/**
 * Merges multiple {@link ConfigurationSource}s. In case of key collision last-match wins merge strategy is used.
 */
public class MergeConfigurationSource implements ConfigurationSource {

  private final ConfigurationSource[] sources;

  /**
   * Create a merge of provided {@link ConfigurationSource}s
   *
   * @param sources configuration sources to merge
   */
  public MergeConfigurationSource(ConfigurationSource... sources) {
    this.sources = requireNonNull(sources);

    for (ConfigurationSource source : sources) {
      requireNonNull(source);
    }
  }

  /**
   * Get configuration set for a given {@code environment} from this source in a form of {@link Properties}. The configuration
   * set is a result of a merge of provided {@link ConfigurationSource} configurations. In case of key collision
   * last-match wins merge strategy is used.
   *
   * @param environment environment to use
   * @return configuration set for {@code environment}
   * @throws MissingEnvironmentException when requested environment couldn't be found
   * @throws IllegalStateException       when unable to fetch configuration from one of the underlying sources
   */
  @Override
  public Properties getConfiguration(Environment environment) {
    Properties properties = new Properties();

    for (ConfigurationSource source : sources) {
      Properties sourceProperties = source.getConfiguration(environment);
      properties.putAll(sourceProperties);
    }

    return properties;
  }

  @Override
  public void init() {
    for (ConfigurationSource source : sources) {
      source.init();
    }
  }

  @Override
  public String toString() {
    return "MergeConfigurationSource{" +
        "sources=" + Arrays.toString(sources) +
        '}';
  }
}
