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
package org.cfg4j.provider;

import org.cfg4j.source.ConfigurationSource;
import org.cfg4j.source.context.environment.DefaultEnvironment;
import org.cfg4j.source.context.environment.Environment;
import org.cfg4j.source.empty.EmptyConfigurationSource;
import org.cfg4j.source.reload.ReloadStrategy;
import org.cfg4j.source.reload.strategy.OnInitReloadStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A builder producing {@link ConfigurationProvider}s. If you don't specify the value for one the fields
 * then the default value will be provided - read the constructor's documentation to learn
 * what the default values are.
 */
public class ConfigurationProviderBuilder {

  private static final Logger LOG = LoggerFactory.getLogger(ConfigurationProviderBuilder.class);

  private ConfigurationSource configurationSource;
  private ReloadStrategy reloadStrategy;
  private Environment environment;

  /**
   * Construct {@link ConfigurationProvider}s builder
   * <p>
   * Default setup (override using with*() methods)
   * <ul>
   * <li>ConfigurationSource: {@link EmptyConfigurationSource}</li>
   * <li>ReloadStrategy: {@link OnInitReloadStrategy}</li>
   * <li>Environment: {@link DefaultEnvironment}</li>
   * </ul>
   */
  public ConfigurationProviderBuilder() {
    configurationSource = new EmptyConfigurationSource();
    reloadStrategy = new OnInitReloadStrategy();
    environment = new DefaultEnvironment();
  }

  /**
   * Set {@link ConfigurationSource} for {@link ConfigurationProvider}s built by this builder
   *
   * @param configurationSource {@link ConfigurationSource} to use
   * @return this builder with {@link ConfigurationSource} set to {@code configurationSource}
   */
  public ConfigurationProviderBuilder withConfigurationSource(ConfigurationSource configurationSource) {
    this.configurationSource = configurationSource;
    return this;
  }

  /**
   * Set {@link ReloadStrategy} for {@link ConfigurationProvider}s built by this builder
   *
   * @param reloadStrategy {@link ReloadStrategy} to use
   * @return this builder with {@link ReloadStrategy} set to {@code reloadStrategy}
   */
  public ConfigurationProviderBuilder withReloadStrategy(ReloadStrategy reloadStrategy) {
    this.reloadStrategy = reloadStrategy;
    return this;
  }

  /**
   * Set {@link Environment} for {@link ConfigurationProvider}s built by this builder
   *
   * @param environment {@link Environment} to use
   * @return this builder with {@link Environment} set to {@code environment}
   */
  public ConfigurationProviderBuilder withEnvironment(Environment environment) {
    this.environment = environment;
    return this;
  }

  /**
   * Build a {@link ConfigurationProvider} using this builder's configuration
   *
   * @return new {@link ConfigurationProvider}
   */
  public ConfigurationProvider build() {
    LOG.info("Initializing ConfigurationProvider with "
        + configurationSource.getClass().getCanonicalName() + " source, "
        + reloadStrategy.getClass().getCanonicalName() + " reload strategy and "
        + environment.getClass().getCanonicalName() + " environment");

    reloadStrategy.init(configurationSource);

    return new SimpleConfigurationProvider(configurationSource, environment);
  }

  @Override
  public String toString() {
    return "ConfigurationProviderBuilder{" +
        "configurationSource=" + configurationSource +
        ", reloadStrategy=" + reloadStrategy +
        ", environment=" + environment +
        '}';
  }
}
