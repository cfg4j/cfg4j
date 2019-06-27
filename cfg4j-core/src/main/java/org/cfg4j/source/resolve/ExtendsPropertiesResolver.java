package org.cfg4j.source.resolve;

import java.util.Map;

/**
 * Property resolver that allows retrieve subset of properies started from provided key. And override some of them with new value. Implements {@link PropertiesResolver}.
 * <p>
 * Source with properties
 * d.a=1
 * d.b.c=2
 * x.^extends=d
 * x.a=11
 * <p>
 * will be resolved to
 * d.a=1
 * d.b.c=2
 * x.a=11
 * x.b.c=2
 * <p>
 * Similar functionality can be achived with {@link LinkPropertiesResolver}. The main difference that linking achieved through property key and allows override.
 */
public class ExtendsPropertiesResolver extends AbstractPropertiesResolver {

  @Override
  protected void resolveProperty(String key, Object value, Map<String, Object> input, Map<String, Object> output) {
    if (containsPlaceholder(key)) {
      String linkPrefix = (String) value;
      String parentKey = key.replaceAll("\\.\\^extends", "");
      putWithKeyFromPrefix(input, output, parentKey, linkPrefix);
    } else {
      output.put(key, value);
    }
  }

  private void putWithKeyFromPrefix(Map<String, Object> input, Map<String, Object> output, String keyToReplace, String linkPrefix) {
    boolean found = false;
    for (Map.Entry<String, Object> entry : input.entrySet()) {
      String key = entry.getKey();
      if (key.startsWith(linkPrefix)) {
        String finalKey = key.replace(linkPrefix, keyToReplace);
        if(input.containsKey(finalKey)) {
          output.put(finalKey, input.get(finalKey));
        } else {
          output.put(finalKey, entry.getValue());
        }
        found = true;
      }
    }
    if (!found) {
      throw new IllegalStateException("Cannot find link values for extends of " + linkPrefix);
    }
  }

  private boolean containsPlaceholder(String key) {
    return key.endsWith("^extends");
  }
}
