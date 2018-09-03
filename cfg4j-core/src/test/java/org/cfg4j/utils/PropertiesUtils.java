package org.cfg4j.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class PropertiesUtils {
  public static Map<String, Object> asMap(String... args) {
    Map<String, Object> result = new HashMap<>();
    for (int i = 1; i < args.length; i += 2) {
      result.put(args[i - 1], args[i]);
    }
    return result;
  }

  public static Properties propertiesWith(String... args) {
    Properties properties = new Properties();
    for (int i = 1; i < args.length; i += 2) {
      properties.put(args[i - 1], args[i]);
    }

    return properties;
  }

}
