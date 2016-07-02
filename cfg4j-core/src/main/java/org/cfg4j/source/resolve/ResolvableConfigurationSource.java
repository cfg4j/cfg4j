package org.cfg4j.source.resolve;

import org.cfg4j.source.ConfigurationSource;
import org.cfg4j.source.context.environment.Environment;
import org.cfg4j.source.context.environment.MissingEnvironmentException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * A {@link ConfigurationSource} that resolves/transforms property keys and values with help of provided {@link PropertiesResolver}s.
 */
public class ResolvableConfigurationSource implements ConfigurationSource {
  private ConfigurationSource underlyingSource;
  private List<PropertiesResolver> resolvers;

  public ResolvableConfigurationSource(ConfigurationSource underlyingSource, List<PropertiesResolver> resolvers) {
    this.underlyingSource = underlyingSource;
    this.resolvers = resolvers;
  }

  /**
   * Get configuration set of resolved properties for a given {@code environment}
   *
   * @param environment environment to use
   * @return configuration set for {@code environment} with already resolved property keys and properties
   * @throws MissingEnvironmentException when there's no config for the given environment in the cache
   */
  @Override
  public Properties getConfiguration(Environment environment) {
    underlyingSource.getConfiguration(environment);
    return resolve(underlyingSource.getConfiguration(environment));
  }

  @SuppressWarnings("unchecked")
  private Properties resolve(Properties sourceProperties) {
    Map<String, Object> result = new HashMap(sourceProperties);
     for(PropertiesResolver resolver: resolvers) {
       result = resolver.resolve(result);
     }
    Properties properties = new Properties();
    properties.putAll(result);
    return properties;
  }

  @Override
  public void init() {
    underlyingSource.init();
  }
}
