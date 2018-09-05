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
package org.cfg4j.provider;

import java.io.File;
import java.lang.reflect.Method;
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
 * Provides access to configuration on a single property level, aggregated and through binding in a format agnostic way.
 */
public interface ConfigurationProvider {

  /**
   * Get full set of configuration represented as {@link Properties}.
   *
   * @return full configuration set
   * @throws IllegalStateException when provider is unable to fetch configuration
   */
  Properties allConfigurationAsProperties();

  /**
   * Get a configuration property of a given basic {@code type}. Sample call could look like:
   * <pre>
   *   boolean myBooleanProperty = configurationProvider.getProperty("my.property", boolean.class);
   * </pre>
   *
   * @param <T>  property type. Supported baic types: {@link BigDecimal}, {@link BigInteger}, {@link Boolean}, {@link Byte},
   *             {@link Character}, {@link Class}, {@link Double}, {@link Enum}, {@link File}, {@link Float}, {@link Integer},
   *             {@link Long}, {@link Number}, {@link Short}, {@link String}, {@link URL}, {@link URI} and arrays.
   *             For {@link Collection} support see method {@link #getProperty(String, GenericTypeInterface)})
   * @param key  configuration key
   * @param type {@link Class} for {@code <T>}
   * @return configuration value
   * @throws NoSuchElementException   when the provided {@code key} doesn't have a corresponding config value
   * @throws IllegalArgumentException when property can't be converted to {@code type}
   * @throws IllegalStateException    when provider is unable to fetch configuration value for the given {@code key}
   */
  <T> T getProperty(String key, Class<T> type);

  /**
   * Get a configuration property of a generic type {@code T}. Sample call could look like:
   * <pre>
   *   List&lt;String&gt; myListProperty = configurationProvider.getProperty("my.list", new GenericType&lt;List&lt;String&gt;&gt;() { });
   * </pre>
   *
   * @param <T>         property type. Supported collections (and most of their standard implementations): {@link Collection},
   *                    {@link List}, {@link Set}, {@link SortedSet}, {@link Map}, {@link SortedMap}
   * @param key         configuration key
   * @param genericType {@link GenericTypeInterface} wrapper for {@code <T>}
   * @return configuration value
   * @throws NoSuchElementException   when the provided {@code key} doesn't have a corresponding config value
   * @throws IllegalArgumentException when property can't be coverted to {@code type}
   * @throws IllegalStateException    when provider is unable to fetch configuration value for the given {@code key}
   */
  <T> T getProperty(String key, GenericTypeInterface genericType);

  /**
   * Create an instance of a given {@code type} that will be bound to this provider. Each time configuration changes the
   * bound object will be updated with the new values. Use {@code prefix} to specify the relative path to configuration
   * values. Please note that each method of returned object can throw runtime exceptions. For details see javadoc for
   * {@link BindInvocationHandler#invoke(Object, Method, Object[])}.
   *
   * @param <T>    interface describing configuration object to bind
   * @param prefix relative path to configuration values (e.g. "myContext" will map settings "myContext.someSetting",
   *               "myContext.someOtherSetting")
   * @param type   {@link Class} for {@code <T>}
   * @return configuration object bound to this {@link ConfigurationProvider}
   * @throws NoSuchElementException   when the provided {@code key} doesn't have a corresponding config value
   * @throws IllegalArgumentException when property can't be coverted to {@code type}
   * @throws IllegalStateException    when provider is unable to fetch configuration value for the given {@code key}
   */
  <T> T bind(String prefix, Class<T> type);
}
