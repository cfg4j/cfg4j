package org.cfg4j.source.resolve;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Property resolver that process placeholders from system properties. Implements {@link PropertiesResolver}.
 *
 * Source with properties
 * d=${test}
 *
 * where system property test=1
 * will be resolved to
 * d=1
 */
public class PlaceholderPropertiesResolver extends AbstractPropertiesResolver {
  private final Pattern placeholderPattern = Pattern.compile("\\$\\{([^.]+)\\}");

  @Override
  protected void resolveProperty(String key, String value, Map<String, String> input, Map<String, String> output) {
    if (containsPlaceholder(value) || containsPlaceholder(key)) {
      output.put(resolve(key), resolve(value));
    } else {
      output.put(key, value);
    }
  }

  private String resolve(String value) {
    Matcher matcher = placeholderPattern.matcher(value);
    String result = value;
    while (matcher.find()) {
      String placeholder = matcher.group(0);
      String placeholderKey = matcher.group(1);
      String propertyValue = System.getProperty(placeholderKey);
      if (propertyValue==null) {
        throw new IllegalStateException("Cannot resolve placeholder " + placeholderKey + " in value " + value);
      }
      result = result.replace(placeholder, propertyValue);

    }
    return result;
  }

  private boolean containsPlaceholder(String value) {
    return value.contains("${");
  }
}
