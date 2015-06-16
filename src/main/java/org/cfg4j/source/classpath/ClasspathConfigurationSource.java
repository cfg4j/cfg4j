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
package org.cfg4j.source.classpath;

import static java.util.Objects.requireNonNull;

import org.cfg4j.source.ConfigurationSource;
import org.cfg4j.source.context.DefaultEnvironment;
import org.cfg4j.source.context.Environment;
import org.cfg4j.source.context.MissingEnvironmentException;
import org.cfg4j.source.git.ConfigFilesProvider;

import java.util.Properties;

/**
 * {@link ConfigurationSource} reading configuration from classpath files.
 */
public class ClasspathConfigurationSource implements ConfigurationSource {

  private final ConfigFilesProvider configFilesProvider;

  /**
   * Construct {@link ConfigurationSource} backed by classpath files. File list should by provided by
   * {@link ConfigFilesProvider} and will be treated as relative paths to the environment provided in
   * {@link #getConfiguration()} and {@link #getConfiguration(Environment)} calls (see corresponding javadocs
   * for detail).
   *
   * @param configFilesProvider
   */
  public ClasspathConfigurationSource(ConfigFilesProvider configFilesProvider) {
    this.configFilesProvider = requireNonNull(configFilesProvider);
  }

  /**
   * <b>DEPRECATED: Use {@link #getConfiguration(Environment)} with {@link DefaultEnvironment} instead.</b>
   * <p>
   * Get configuration set from this source in a form of {@link Properties}. Uses default environment which means
   * treating paths from {@link ConfigFilesProvider} as absolute paths.
   *
   * @return configuration set for default environment
   * @throws IllegalStateException when unable to fetch configuration
   */
  @Override
  public Properties getConfiguration() {
    return getConfiguration(new DefaultEnvironment());
  }

  /**
   * Get configuration set for a given {@code environment} from this source in a form of {@link Properties}.
   * Path provided by {@code environment} is prepended to all file paths from {@link ConfigFilesProvider} used
   * at construction time.
   *
   * @param environment environment to use
   * @return configuration set for {@code environment}
   * @throws MissingEnvironmentException when requested environment couldn't be found
   * @throws IllegalStateException       when unable to fetch configuration
   */
  @Override
  public Properties getConfiguration(Environment environment) {
    return null;
  }

  @Override
  public void refresh() {
    // NOP
  }
}
