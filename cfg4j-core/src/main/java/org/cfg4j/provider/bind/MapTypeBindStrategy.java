package org.cfg4j.provider.bind;

import org.cfg4j.provider.ConfigurationProvider;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

public class MapTypeBindStrategy extends PrefixBasedBindStrategy {
  @Override
  public boolean canApply(Method method) {
    return "java.util.Map".equals(method.getReturnType().getName());
  }

  @Override
  public Object apply(Method method, String prefix, ConfigurationProvider configurationProvider) {
    Type[] typeArguments = ((ParameterizedType) method.getGenericReturnType()).getActualTypeArguments();
    Class<?> keyClass = (Class<?>) typeArguments[0];
    if (!String.class.equals(keyClass)) {
      throw new IllegalStateException("Invalid bind interface " + method.getDeclaringClass() +". Map properties should have key of String type.");
    }
    Class<?> valueClass = (Class<?>) typeArguments[1];
    String mapPrefix = buildPrefix(prefix, method.getName());
    Set<String> keyPathes = findMapKeyPathes(configurationProvider.allConfigurationAsProperties(), mapPrefix);
    HashMap<String, Object> proxyMap = new HashMap<>();
    for (String keyPath : keyPathes) {
      proxyMap.put(key(keyPath), configurationProvider.bind(keyPath, valueClass));
    }
    return proxyMap;
  }

  private String key(String keyPath) {
    int startIndex = keyPath.lastIndexOf(".");
    return keyPath.substring(startIndex == -1 ? 0 : startIndex+1);
  }

  private Set<String> findMapKeyPathes(Properties properties, String prefix) {
    Set<String> result = new HashSet<>();
    for (Map.Entry<Object, Object> entry :properties.entrySet()) {
      String key = (String) entry.getKey();
      String mapKeyPrefix = prefix + ".";
      if (key.startsWith(mapKeyPrefix)) {
        result.add(getKeyPath(key, mapKeyPrefix));
      }
    }
    return result;
  }

  private String getKeyPath(String key, String mapKeyPrefix) {
    int endIndex = key.indexOf(".", mapKeyPrefix.length() + 1);
    return key.substring(0, endIndex == -1 ? key.length() : endIndex);
  }

}
