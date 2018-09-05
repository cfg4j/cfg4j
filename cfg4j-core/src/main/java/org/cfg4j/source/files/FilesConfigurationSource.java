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
package org.cfg4j.source.files;

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

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

/**
 * {@link ConfigurationSource} reading configuration from local files.
 */
public class FilesConfigurationSource implements ConfigurationSource {

  private final ConfigFilesProvider configFilesProvider;
  private final PropertiesProviderSelector propertiesProviderSelector;

  /**
   * Construct {@link ConfigurationSource} backed by files. Uses "application.properties" file
   * located in the path specified by the {@link Environment} provided to {@link #getConfiguration(Environment)}
   * calls (see corresponding javadoc for detail).
   */
  public FilesConfigurationSource() {
    this(() -> Collections.singletonList(
        Paths.get("application.properties")
    ));
  }

  /**
   * Construct {@link ConfigurationSource} backed by files. File paths should by provided by
   * {@link ConfigFilesProvider} and will be treated as relative paths to the environment provided in
   * {@link #getConfiguration(Environment)} calls (see corresponding javadoc for detail). Configuration
   * file type is detected using file extension (see {@link PropertiesProviderSelector}).
   *
   * @param configFilesProvider {@link ConfigFilesProvider} supplying a list of configuration files to use
   */
  public FilesConfigurationSource(ConfigFilesProvider configFilesProvider) {
    this(configFilesProvider, new PropertiesProviderSelector(
        new PropertyBasedPropertiesProvider(), new YamlBasedPropertiesProvider(), new JsonBasedPropertiesProvider()
    ));
  }

  /**
   * Construct {@link ConfigurationSource} backed by files. File paths should by provided by
   * {@link ConfigFilesProvider} and will be treated as relative paths to the environment provided in
   * {@link #getConfiguration(Environment)} calls (see corresponding javadoc for detail). Configuration
   * file type is detected using file extension (see {@link PropertiesProviderSelector}).
   *
   * @param configFilesProvider        {@link ConfigFilesProvider} supplying a list of configuration files to use
   * @param propertiesProviderSelector selector used for choosing {@link PropertiesProvider} based on a configuration file extension
   */
  public FilesConfigurationSource(ConfigFilesProvider configFilesProvider, PropertiesProviderSelector propertiesProviderSelector) {
    this.configFilesProvider = requireNonNull(configFilesProvider);
    this.propertiesProviderSelector = requireNonNull(propertiesProviderSelector);
  }

  /**
   * Get configuration set for a given {@code environment} from this source in a form of {@link Properties}.
   * {@link Environment} name is prepended to all file paths from {@link ConfigFilesProvider}
   * to form an absolute configuration file path. If environment name is empty paths are treated as relative
   * to the user's home directory location.
   *
   * @param environment environment to use
   * @return configuration set for {@code environment}
   * @throws MissingEnvironmentException when requested environment couldn't be found
   * @throws IllegalStateException       when unable to fetch configuration
   */
  @Override
  public Properties getConfiguration(Environment environment) {
    Properties properties = new Properties();

    Path rootPath;
    if (environment.getName().trim().isEmpty()) {
      rootPath = Paths.get(System.getProperty("user.home"));
    } else {
      rootPath = Paths.get(environment.getName());
    }

    if (!rootPath.toFile().exists()) {
      throw new MissingEnvironmentException("Directory doesn't exist: " + rootPath);
    }

    List<Path> paths = new ArrayList<>();
    for (Path path : configFilesProvider.getConfigFiles()) {
      paths.add(rootPath.resolve(path));
    }

    for (Path path : paths) {
      try (InputStream input = new FileInputStream(path.toFile())) {

        PropertiesProvider provider = propertiesProviderSelector.getProvider(path.getFileName().toString());
        properties.putAll(provider.getProperties(input));

      } catch (IOException e) {
        throw new IllegalStateException("Unable to load properties from file: " + path, e);
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
    return "FilesConfigurationSource{" +
        "configFilesProvider=" + configFilesProvider +
        '}';
  }
}
