package org.cfg4j.source.resolve;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Property resolver that process placeholders and resolve them from system properties or all properties found in processed sources.
 * Implements {@link PropertiesResolver}.
 *
 * Source with properties
 * d=${test}
 *
 * where system property test=1
 * will be resolved to
 * d=1
 *
 *
 * Source with properties
 * a=1
 * a.b=${a}
 *
 * will be resolved to
 * a=1
 * a.b=1
 *
 * Source with properties
 * a=1
 * a.b=${a}
 * a.b.c=${a.b}
 *
 * will be resolved to
 * a=1
 * a.b=1
 * a.b.c=1
 *
 * Source with properties
 * a=1
 * b=2
 * c=${a}+${b}
 *
 * will be resolved to
 * a=1
 * b=2
 * c=1+2


 */
public class PlaceholderPropertiesResolver extends AbstractPropertiesResolver {
  private final Pattern placeholderPattern = Pattern.compile("\\$\\{([\\w$\\.]+)\\}");

  @Override
  protected void resolveProperty(String key, Object value, Map<String, Object> input, Map<String, Object> output) {
    if (value instanceof String && containsPlaceholder((String) value)) {
      output.put(key, resolve((String) value, input, output));
    } else {
      output.put(key, value);
    }
  }

  private String resolve(String value, Map<String, Object> input, Map<String, Object> output) {
    Matcher matcher = placeholderPattern.matcher(value);
    String result = value;
    while (matcher.find()) {
      String placeholder = matcher.group(0);
      String placeholderKey = matcher.group(1);
      String propertyValue = findPropertyByKey(placeholderKey, input, output);
      result = result.replace(placeholder, propertyValue);
      if (containsPlaceholder(result)) {
        result = resolve(result, input, output);
      }
    }
    return result;
  }

  private String findPropertyByKey(String key, Map<String, Object> input, Map<String, Object> output) {
    String value = System.getProperty(key);
    if (value == null) {
      value = (String) output.get(key);
      if (value == null) {
        value = (String) input.get(key);
      }
    }
    if (value == null) {
      throw new IllegalStateException("Cannot resolve placeholder " + key);
    }
    return value;
  }

  private boolean containsPlaceholder(String value) {
    return value.contains("${");
  }
}
