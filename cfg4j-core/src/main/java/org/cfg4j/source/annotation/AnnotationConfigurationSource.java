package org.cfg4j.source.annotation;

import java.lang.reflect.Method;
import java.util.Properties;

import org.cfg4j.source.ConfigurationSource;
import org.cfg4j.source.context.environment.Environment;

/**
 * Extracts configuration values from annotated configuration classes.
 * 
 * <pre>
 * <code>
 *  public interface MyConfig {
 *   
 *    &#64;DefaultValue("true")
 *    public boolean enabled();
 *   
 *    &#64;DefaultNull
 *    public String name()
 *  }
 * </code>
 * </pre>
 */
public class AnnotationConfigurationSource implements ConfigurationSource {

  private final String prefix;

  private Class<?> configClass;

  private Properties properties;

  /**
   * @param configClass
   * @param prefix
   */
  public AnnotationConfigurationSource(Class<?> configClass, String prefix) {
    this.prefix = prefix;
    this.configClass = configClass;
  }

  @Override
  public void init() {
    properties = new Properties();
    addDefaults(prefix, configClass);
  }

  private void addDefaults(String keyPrefix, Class<?> currentClass) {
    for (Method method : currentClass.getMethods()) {
      String key = concat(keyPrefix, method.getName());
      if (isConfigEntry(method)) {
        addEntryDefault(key, method);
      } else if (isNestedConfig(method)) {
        addDefaults(key, method.getReturnType());
        addNestedDefaults(key, method);
      }
    }
  }

  protected boolean isNestedConfig(Method method) {
    return false;
  }

  protected boolean isConfigEntry(Method method) {
    return method.getDeclaringClass() != Object.class;
  }

  private void addNestedDefaults(String key, Method method) {
    DefaultValue[] values = method.getAnnotationsByType(DefaultValue.class);
    for (DefaultValue value : values) {
      if (value.key().isEmpty()) {
        throw new IllegalStateException("defaultValues must specify a key for " + key);
      }
      properties.put(concat(key, value.key()), value.value());
    }

    DefaultNull[] nulls = method.getAnnotationsByType(DefaultNull.class);
    for (DefaultNull value : nulls) {
      if (value.key().isEmpty()) {
        throw new IllegalStateException("defaultNulls must specify a key for " + key);
      }
      properties.put(concat(key, value.key()), null);
    }
  }

  private static String concat(String prefix, String name) {
    return prefix + (prefix.isEmpty() ? "" : ".") + name;
  }

  private void addEntryDefault(String key, Method method) {
    DefaultNull defaultNull = method.getAnnotation(DefaultNull.class);
    DefaultValue defaultValue = method.getAnnotation(DefaultValue.class);
    if (defaultNull != null && defaultValue != null) {
      throw new IllegalStateException("cannot have both @DefaultValue and @DefaultNull for " + key);
    }
    if (defaultValue != null) {
      if (!defaultValue.key().isEmpty()) {
        throw new IllegalStateException("defaultValue cannot specify a key for " + key);
      }
      properties.put(key, defaultValue.value());
    }
    if (defaultNull != null) {
      properties.put(key, "");
    }
  }

  @Override
  public Properties getConfiguration(Environment environment) {
    return properties;
  }

}
