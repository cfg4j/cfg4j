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
package org.cfg4j.source.classpath;

import static java.util.Objects.requireNonNull;

import org.cfg4j.source.ConfigurationSource;
import org.cfg4j.source.context.environment.Environment;
import org.cfg4j.source.context.environment.MissingEnvironmentException;
import org.cfg4j.source.context.filesprovider.ConfigFilesProvider;
import org.cfg4j.source.context.propertiesprovider.JsonBasedPropertiesProvider;
import org.cfg4j.source.context.propertiesprovider.PropertiesProvider;
import org.cfg4j.source.context.propertiesprovider.PropertiesProviderSelector;
import org.cfg4j.source.context.propertiesprovider.PropertyBasedPropertiesProvider;
import org.cfg4j.source.context.propertiesprovider.YamlBasedPropertiesProvider;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

/**
 * {@link ConfigurationSource} reading configuration from classpath files.
 * <p>
 * Environment resolution happens as follows:
 * {@link Environment} name is prepended to all file paths from {@link ConfigFilesProvider}
 * to form an absolute configuration file path. Trailing slashes in environment name are not supported (due
 * to Java disallowing classpath locations starting with slash).
 */
public class ClasspathConfigurationSource implements ConfigurationSource {

  private final ConfigFilesProvider configFilesProvider;
  private final PropertiesProviderSelector propertiesProviderSelector;

  /**
   * Construct {@link ConfigurationSource} backed by classpath files. Uses "application.properties" file
   * located in the path specified by the {@link Environment} provided to {@link #getConfiguration(Environment)}
   * calls (see corresponding javadoc for detail).
   */
  public ClasspathConfigurationSource() {
    this(() -> Collections.singletonList(
        Paths.get("application.properties")
    ));
  }

  /**
   * Construct {@link ConfigurationSource} backed by classpath files. File paths should by provided by
   * {@link ConfigFilesProvider} and will be treated as relative paths to the environment provided in
   * {@link #getConfiguration(Environment)} calls (see corresponding javadoc for detail). Configuration
   * file type is detected using file extension (see {@link PropertiesProviderSelector}).
   *
   * @param configFilesProvider {@link ConfigFilesProvider} supplying a list of configuration files to use
   */
  public ClasspathConfigurationSource(ConfigFilesProvider configFilesProvider) {
    this(configFilesProvider, new PropertiesProviderSelector(
        new PropertyBasedPropertiesProvider(), new YamlBasedPropertiesProvider(), new JsonBasedPropertiesProvider()
    ));
  }

  /**
   * Construct {@link ConfigurationSource} backed by classpath files. File paths should by provided by
   * {@link ConfigFilesProvider} and will be treated as relative paths to the environment provided in
   * {@link #getConfiguration(Environment)} calls (see corresponding javadoc for detail).
   *
   * @param configFilesProvider        {@link ConfigFilesProvider} supplying a list of configuration files to use
   * @param propertiesProviderSelector selector used for choosing {@link PropertiesProvider} based on a configuration file extension
   */
  public ClasspathConfigurationSource(ConfigFilesProvider configFilesProvider, PropertiesProviderSelector propertiesProviderSelector) {
    this.configFilesProvider = requireNonNull(configFilesProvider);
    this.propertiesProviderSelector = requireNonNull(propertiesProviderSelector);
  }

  /**
   * Get configuration from classpath. See the class-level javadoc for detail on environment resolution.
   *
   * @param environment environment to fetch configuration for
   * @return configuration set for {@code environment}
   * @throws MissingEnvironmentException when requested environment couldn't be found
   * @throws IllegalStateException       when unable to fetch configuration
   */
  @Override
  public Properties getConfiguration(Environment environment) {
    Properties properties = new Properties();

    Path pathPrefix = Paths.get(environment.getName());

    URL url = getClass().getClassLoader().getResource(pathPrefix.toString());
    if (url == null && !environment.getName().isEmpty()) {
      throw new MissingEnvironmentException("Directory doesn't exist: " + environment.getName());
    }

    List<Path> paths = new ArrayList<>();
    for (Path path : configFilesProvider.getConfigFiles()) {
      paths.add(pathPrefix.resolve(path));
    }

    for (Path path : paths) {
      try (InputStream input = getClass().getClassLoader().getResourceAsStream(path.toString())) {

        if (input == null) {
          throw new IllegalStateException("Unable to load properties from classpath: " + path);
        }

        PropertiesProvider provider = propertiesProviderSelector.getProvider(path.getFileName().toString());
        properties.putAll(provider.getProperties(input));

      } catch (IOException e) {
        throw new IllegalStateException("Unable to load properties from classpath: " + path, e);
      }
    }

    return properties;
  }

  @Override
  public void init() {
    // NOP
  }

  @Override
  public String toString() {
    return "ClasspathConfigurationSource{" +
        "configFilesProvider=" + configFilesProvider +
        '}';
  }
}
