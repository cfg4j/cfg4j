package org.cfg4j.source.context.propertiesprovider;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigValue;

public class HoconBasedPropertiesProvider extends FormatBasedPropertiesProvider {

  /**
   * Get {@link Properties} for a given {@code inputStream} treating it as a HOCON file.
   *
   * @param inputStream input stream representing HOCON file
   * @return properties representing values from {@code inputStream}
   */
  @Override
  public Properties getProperties(InputStream inputStream) {
    Config config = ConfigFactory.parseReader(new InputStreamReader(inputStream));
    final Set<Map.Entry<String, ConfigValue>> entries = config.entrySet();

    Properties properties = new Properties();

    Map<String, Object> map = new ConcurrentHashMap<>();
    for (Map.Entry<String, ConfigValue> entry : entries) {
      map.put(entry.getKey(), entry.getValue().unwrapped());
    }
    properties.putAll(flatten(map));

    return properties;
  }
}
