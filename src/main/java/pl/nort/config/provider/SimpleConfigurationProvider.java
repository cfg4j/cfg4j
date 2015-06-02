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

import static com.google.common.base.Preconditions.checkNotNull;

import com.github.drapostolos.typeparser.NoSuchRegisteredParserException;
import com.github.drapostolos.typeparser.TypeParser;
import com.github.drapostolos.typeparser.TypeParserException;
import pl.nort.config.source.ConfigurationSource;

import java.util.NoSuchElementException;
import java.util.Properties;

/**
 * Basic implementation of {@link ConfigurationProvider}
 */
public class SimpleConfigurationProvider implements ConfigurationProvider {

  private final ConfigurationSource configurationSource;

  /**
   * {@link ConfigurationProvider} backed by provided {@link ConfigurationSource}
   *
   * @param configurationSource source for configuration
   */
  public SimpleConfigurationProvider(ConfigurationSource configurationSource) {
    this.configurationSource = checkNotNull(configurationSource);
  }

  @Override
  public Properties allConfigurationAsProperties() {
    try {
      return configurationSource.getConfiguration();
    } catch (IllegalStateException e) {
      throw new IllegalStateException("Couldn't fetch configuration from configuration source", e);
    }
  }

  @Override
  public String getProperty(String key) {
    try {

      String property = configurationSource.getConfiguration().getProperty(key);

      if (property == null) {
        throw new NoSuchElementException("No configuration with key: " + key);
      }

      return property;

    } catch (IllegalStateException e) {
      throw new IllegalStateException("Couldn't fetch configuration from configuration source for key: " + key, e);
    }
  }

  @Override
  public <T> T getProperty(String key, Class<T> type) {
    String propertyStr = getProperty(key);

    try {
      TypeParser parser = TypeParser.newBuilder().build();
      return parser.parse(propertyStr, type);
    } catch (TypeParserException | NoSuchRegisteredParserException e) {
      throw new IllegalArgumentException("Unable to cast value \'" + propertyStr + "\' to " + type, e);
    }
  }

  @Override
  public <T> T getProperty(String key, GenericType<T> genericType) {
    String propertyStr = getProperty(key);

    try {
      TypeParser parser = TypeParser.newBuilder().build();
      @SuppressWarnings("unchecked")
      T property = (T) parser.parseType(propertyStr, genericType.getType());
      return property;
    } catch (TypeParserException | NoSuchRegisteredParserException e) {
      throw new IllegalArgumentException("Unable to cast value \'" + propertyStr + "\' to " + genericType, e);
    }
  }

  @Override
  public <T> void bind(String s, Class<T> type) {

  }
}
