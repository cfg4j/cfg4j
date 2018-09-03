/*
 * Copyright 2015-2016 Norbert Potocki (norbert.potocki@nort.pl)
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

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import org.cfg4j.provider.bind.MapTypeBindStrategy;
import org.cfg4j.source.ConfigurationSource;
import org.cfg4j.source.context.environment.DefaultEnvironment;
import org.cfg4j.source.context.environment.Environment;
import org.cfg4j.source.empty.EmptyConfigurationSource;
import org.cfg4j.source.metered.MeteredConfigurationSource;
import org.cfg4j.source.reload.CachedConfigurationSource;
import org.cfg4j.source.reload.MeteredReloadable;
import org.cfg4j.source.reload.ReloadStrategy;
import org.cfg4j.source.reload.Reloadable;
import org.cfg4j.source.reload.strategy.ImmediateReloadStrategy;
import org.cfg4j.source.resolve.PropertiesResolver;
import org.cfg4j.source.resolve.ResolvableConfigurationSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.requireNonNull;

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
  private MetricRegistry metricRegistry;
  private List<BindStrategy> bindStrategies;
  private String prefix;
  private List<PropertiesResolver> propertyResolvers;

  /**
   * Construct {@link ConfigurationProvider}s builder.
   * Default setup (override using with*() methods)
   * <ul>
   * <li>ConfigurationSource: {@link EmptyConfigurationSource}</li>
   * <li>ReloadStrategy: {@link ImmediateReloadStrategy}</li>
   * <li>Environment: {@link DefaultEnvironment}</li>
   * <li>Metrics: disabled</li>
   * </ul>
   */
  public ConfigurationProviderBuilder() {
    configurationSource = new EmptyConfigurationSource();
    reloadStrategy = new ImmediateReloadStrategy();
    environment = new DefaultEnvironment();
    prefix = "";
    bindStrategies = new ArrayList<>();
    propertyResolvers = new ArrayList<>();
  }

  /**
   * Set {@link ConfigurationSource} for {@link ConfigurationProvider}s built by this builder.
   *
   * @param configurationSource {@link ConfigurationSource} to use
   * @return this builder with {@link ConfigurationSource} set to {@code configurationSource}
   */
  public ConfigurationProviderBuilder withConfigurationSource(ConfigurationSource configurationSource) {
    this.configurationSource = configurationSource;
    return this;
  }

  /**
   * Set {@link ReloadStrategy} for {@link ConfigurationProvider}s built by this builder.
   *
   * @param reloadStrategy {@link ReloadStrategy} to use
   * @return this builder with {@link ReloadStrategy} set to {@code reloadStrategy}
   */
  public ConfigurationProviderBuilder withReloadStrategy(ReloadStrategy reloadStrategy) {
    this.reloadStrategy = reloadStrategy;
    return this;
  }

  /**
   * Set {@link Environment} for {@link ConfigurationProvider}s built by this builder.
   *
   * @param environment {@link Environment} to use
   * @return this builder with {@link Environment} set to {@code environment}
   */
  public ConfigurationProviderBuilder withEnvironment(Environment environment) {
    this.environment = environment;
    return this;
  }

  /**
   * Set {@link Environment} for {@link ConfigurationProvider}s built by this builder.
   *
   * @param environment {@link Environment} to use
   * @return this builder with {@link Environment} set to {@code environment}
   */

  /**
   * Enable metrics emission for {@link ConfigurationProvider}s built by this builder. All metrics will be registered
   * with {@code metricRegistry} and prefixed by {@code prefix}. Provider built by this builder will emit the following metrics:
   * <p>Provider-level metrics:</p>
   * <ul>
   * <li>allConfigurationAsProperties</li>
   * <li>getProperty</li>
   * <li>getPropertyGeneric</li>
   * <li>bind</li>
   * </ul>
   * <p>Source-level metrics</p>
   * <ul>
   * <li>source.getConfiguration</li>
   * <li>source.init</li>
   * <li>source.reload</li>
   * </ul>
   * Each of those metrics is of {@link Timer} type (i.e. includes execution time percentiles, execution count, etc.)
   *
   * @param metricRegistry metric registry for registering metrics
   * @param prefix         prefix for metric names
   * @return this builder
   */
  public ConfigurationProviderBuilder withMetrics(MetricRegistry metricRegistry, String prefix) {
    this.prefix = requireNonNull(prefix);
    this.metricRegistry = metricRegistry;
    return this;
  }

  /**
   * Add {@link BindStrategy} to the list of executed for {@link ConfigurationProvider}s built by this builder. Binders executed in order of addition them with help of this method
   *
   * @param bindStrategy {@link BindStrategy} to use
   * @return this builder
   */
  public ConfigurationProviderBuilder withBindStrategy(BindStrategy bindStrategy) {
    requireNonNull(bindStrategy);
    bindStrategies.add(bindStrategy);
    return this;
  }

  /**
   * Add {@link PropertiesResolver} to the list of executed for {@link ConfigurationProvider}s built by this builder. PropertyResolvers executed in order of addition them with help of this method
   *
   * @param propertyResolver {@link PropertiesResolver} to use
   * @return this builder
   */
  public ConfigurationProviderBuilder withPropertyResolver(PropertiesResolver propertyResolver) {
    requireNonNull(propertyResolver);
    propertyResolvers.add(propertyResolver);
    return this;
  }

  /**
   * Build a {@link ConfigurationProvider} using this builder's configuration.
   *
   * @return new {@link ConfigurationProvider}
   */
  public ConfigurationProvider build() {
    LOG.info("Initializing ConfigurationProvider with "
      + configurationSource.getClass().getCanonicalName() + " source, "
      + reloadStrategy.getClass().getCanonicalName() + " reload strategy and "
      + environment.getClass().getCanonicalName() + " environment");

    if (!propertyResolvers.isEmpty()) {
      configurationSource = new ResolvableConfigurationSource(configurationSource, propertyResolvers);
    }
    final CachedConfigurationSource cachedConfigurationSource = new CachedConfigurationSource(configurationSource);
    if (metricRegistry != null) {
      configurationSource = new MeteredConfigurationSource(metricRegistry, prefix, cachedConfigurationSource);
    }
    cachedConfigurationSource.init();

    Reloadable reloadable = new Reloadable() {
      @Override
      public void reload() {
        cachedConfigurationSource.reload(environment);
      }
    };

    if (metricRegistry != null) {
      reloadable = new MeteredReloadable(metricRegistry, prefix, reloadable);
    }
    reloadable.reload();
    reloadStrategy.register(reloadable);

    bindStrategies.addAll(defaultBindStrategies());
    SimpleConfigurationProvider configurationProvider = new SimpleConfigurationProvider(configurationSource, environment, bindStrategies);
    if (metricRegistry != null) {
      return new MeteredConfigurationProvider(metricRegistry, prefix, configurationProvider);
    }

    return configurationProvider;
  }

  private List<BindStrategy> defaultBindStrategies() {
    List<BindStrategy> bindStrategies = new ArrayList<>();
    bindStrategies.add(new MapTypeBindStrategy());
    return bindStrategies;
  }

  @Override
  public String toString() {
    return "ConfigurationProviderBuilder{" +
      "configurationSource=" + configurationSource +
      ", reloadStrategy=" + reloadStrategy +
      ", environment=" + environment +
      ", metricRegistry=" + metricRegistry +
      ", prefix='" + prefix + '\'' +
      '}';
  }
}
