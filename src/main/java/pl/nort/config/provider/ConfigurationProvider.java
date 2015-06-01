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
package pl.nort.config.provider;

import java.io.File;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;
import java.net.URL;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Properties;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;

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

  /**
   * Get a configuration property of a given basic {@code type}
   *
   * @param key  configuration key
   * @param type property type. Supported baic types: {@link BigDecimal}, {@link BigInteger}, {@link Boolean}, {@link Byte},
   *             {@link Character}, {@link Class}, {@link Double}, {@link Enum}, {@link File}, {@link Float}, {@link Integer},
   *             {@link Long}, {@link Number}, {@link Short}, {@link String}, {@link URL}, {@link URI} and arrays.
   *             For {@link Collection} support see method {@link #getProperty(String, GenericType)})
   * @return configuration value
   * @throws NoSuchElementException when the provided {@code key} doesn't have a corresponding config value
   * @throws IllegalArgumentException when property can't be coverted to {@code type}
   * @throws IllegalStateException  when provider is unable to fetch configuration value for the given {@code key}
   */
  <T> T getProperty(String key, Class<T> type);

  /**
   * Get a configuration property of a generic type {@code T}
   *
   * @param key  configuration key
   * @param genericType property type. Supported collections (and most of their standard implementations): {@link Collection},
   *             {@link List}, {@link Set}, {@link SortedSet}, {@link Map}, {@link SortedMap}
   * @return configuration value
   * @throws NoSuchElementException when the provided {@code key} doesn't have a corresponding config value
   * @throws IllegalArgumentException when property can't be coverted to {@code type}
   * @throws IllegalStateException  when provider is unable to fetch configuration value for the given {@code key}
   */
  <T> T getProperty(String key, GenericType<T> genericType);
}
