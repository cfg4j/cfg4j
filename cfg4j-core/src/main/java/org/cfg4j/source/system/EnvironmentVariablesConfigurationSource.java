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
package org.cfg4j.source.system;

import org.cfg4j.source.ConfigurationSource;
import org.cfg4j.source.context.environment.Environment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * {@link ConfigurationSource} providing all environment variables under the specified {@link Environment}
 * namespace prefix.
 */
public class EnvironmentVariablesConfigurationSource implements ConfigurationSource {

  private static final Logger LOG = LoggerFactory.getLogger(EnvironmentVariablesConfigurationSource.class);
  private final static char ENV_DELIMITER = '_';
  private final static char PROPERTIES_DELIMITER = '.';

  private Map<String, String> environmentVariables = new HashMap<>();
  private boolean initialized = false;

  @Override
  public Properties getConfiguration(Environment environment) {
    if (!initialized) {
      throw new IllegalStateException("Configuration source has to be successfully initialized before you request configuration.");
    }

    environmentVariables.clear();
    environmentVariables.putAll(System.getenv());

    Properties properties = new Properties();

    String environmentContext = formatEnvironmentContext(environment);

    for (Map.Entry<String, String> entry : environmentVariables.entrySet()) {
      if (entry.getKey().startsWith(environmentContext)) {
        properties.put(convertToPropertiesKey(entry.getKey(), environmentContext), entry.getValue());
      }
    }

    return properties;
  }

  @Override
  public void init() {
    LOG.debug("Initializing: Environment Variable Configuration Source");
    initialized = true;
  }

  @Override
  public String toString() {
    return "EnvironmentVariablesConfigurationSource{}";
  }

  /**
   * Convert the Environment Variable Name to the expected Properties Key formatting
   *
   * @param environmentVariableKey The Env Variable Name, possibly prefixed with the {@link Environment} which
   *                               in this context serves as a way to namespace variables
   * @param environmentContext     The Environment context in format: ENVIRONMENTNAME_
   * @return A {@link String} with the environment prefix removed and all underscores converted to periods
   */
  private static String convertToPropertiesKey(String environmentVariableKey, String environmentContext) {
    return environmentVariableKey.substring(environmentContext.length()).replace(ENV_DELIMITER, PROPERTIES_DELIMITER);
  }

  /**
   * Format the provided {@link Environment} to ensure it ends with an underscore
   *
   * @param environment The provided {@link Environment} context
   * @return The formatted {@link String} of the {@link Environment} context
   */
  private static String formatEnvironmentContext(Environment environment) {
    String environmentName = environment.getName();

    if (environmentName == null || environmentName.isEmpty()) {
      return "";
    } else {
      return environmentName.endsWith("_") ? environmentName : environmentName + "_";
    }
  }
}
