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
package pl.nort;

import java.util.NoSuchElementException;
import java.util.Properties;

/**
 * Provides access to configuration values both on a single property level and aggregated.
 */
public interface ConfigurationProvider {

  /**
   * Get full set of configuration represented as {@link Properties}
   *
   * @return full configuration set
   * @throws IllegalStateException when provider is unable to fetch configuration
   */
  Properties allConfigurationAsProperties();

  /**
   * Get {@link String} configuration property
   *
   * @param key configuration key
   * @return configuration value
   * @throws NoSuchElementException when the provided {@code key} doesn't have a corresponding config value
   * @throws IllegalStateException  when provider is unable to fetch configuration value for the given {@code key}
   */
  String getProperty(String key);

}
