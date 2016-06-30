package org.cfg4j.source.resolve;

import java.util.HashMap;
import java.util.Map;

public abstract class AbstractPropertiesResolver implements PropertiesResolver {
  @Override
  public Map<String, String> resolve(Map<String, String> sourceProperties) {
    Map<String, String> result = new HashMap<>();
    for (Map.Entry<String, String> entry : sourceProperties.entrySet()) {
      String key = entry.getKey();
      String value = entry.getValue();
      resolveProperty(key, value, sourceProperties, result);
    }
    return result;
  }

  protected abstract void resolveProperty(String key, String value, Map<String, String> input, Map<String, String> output);
}
