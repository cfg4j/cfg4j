package org.cfg4j.source;

import org.cfg4j.source.context.environment.Environment;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public abstract class BaseConfigurationSource implements ConfigurationSource {

  private final Map<Environment, Properties> cachedConfigurationPerEnvironment;

  public BaseConfigurationSource() {
    cachedConfigurationPerEnvironment = new HashMap<>();
  }

  @Override
  public Properties getConfiguration(Environment environment) {
    return cachedConfigurationPerEnvironment.get(environment);
  }

  @Override
  public void reload(Environment environment) {
    cachedConfigurationPerEnvironment.put(environment, fetchConfiguration(environment));
  }

  protected abstract Properties fetchConfiguration(Environment environment);
}
