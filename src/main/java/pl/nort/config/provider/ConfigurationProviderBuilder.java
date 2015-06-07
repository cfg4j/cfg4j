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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.nort.config.source.ConfigurationSource;
import pl.nort.config.source.EmptyConfigurationSource;
import pl.nort.config.source.context.DefaultEnvironment;
import pl.nort.config.source.context.Environment;
import pl.nort.config.source.refresh.RefreshStrategy;
import pl.nort.config.source.refresh.strategy.OnInitRefreshStrategy;

/**
 * A builder producing {@link ConfigurationProvider}s. If you don't specify the value for one the fields
 * then the default value will be provided - read the constructor's documentation to learn
 * what the default values are.
 */
public class ConfigurationProviderBuilder {

  private static final Logger LOG = LoggerFactory.getLogger(ConfigurationProviderBuilder.class);

  private ConfigurationSource configurationSource;
  private RefreshStrategy refreshStrategy;
  private Environment environment;

  /**
   * Construct {@link ConfigurationProvider}s builder
   * <p>
   * Default setup (override using with*() methods)
   * <ul>
   * <li>ConfigurationSource: {@link EmptyConfigurationSource}</li>
   * <li>RefreshStrategy: {@link OnInitRefreshStrategy}</li>
   * <li>Environment: {@link DefaultEnvironment}</li>
   * </ul>
   */
  public ConfigurationProviderBuilder() {
    configurationSource = new EmptyConfigurationSource();
    refreshStrategy = new OnInitRefreshStrategy();
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
   * Set {@link RefreshStrategy} for {@link ConfigurationProvider}s built by this builder
   *
   * @param refreshStrategy {@link RefreshStrategy} to use
   * @return this builder with {@link RefreshStrategy} set to {@code refreshStrategy}
   */
  public ConfigurationProviderBuilder withRefreshStrategy(RefreshStrategy refreshStrategy) {
    this.refreshStrategy = refreshStrategy;
    return this;
  }

  /**
   * Set {@link Environment} for {@link ConfigurationProviders}s built by this builder
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
    LOG.info("Initializing ConfigurationProvider with " + configurationSource.getClass() + " source, " +
        refreshStrategy.getClass() + " refresh strategy and " + environment.getClass() + " environment" +
        "selection strategy.");

    refreshStrategy.init(configurationSource);

    return new SimpleConfigurationProvider(configurationSource, environment);
  }

}
