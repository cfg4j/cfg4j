package org.cfg4j.source.resolve;

import java.util.HashMap;
import java.util.Map;

public abstract class AbstractPropertiesResolver implements PropertiesResolver {
  @Override
  public Map<String, Object> resolve(Map<String, Object> sourceProperties) {
    Map<String, Object> result = new HashMap<>();
    for (Map.Entry<String, Object> entry : sourceProperties.entrySet()) {
      String key = entry.getKey();
      Object value = entry.getValue();
      resolveProperty(key, value, sourceProperties, result);
    }
    return result;
  }

  protected abstract void resolveProperty(String key, Object value, Map<String, Object> input, Map<String, Object> output);
}
