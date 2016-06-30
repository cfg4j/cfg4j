package org.cfg4j.source.resolve;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Property resolver that process links. Implements {@link PropertiesResolver}.
 *
 * Source with properties
 * d=1
 * a.b.c=$link(d)
 *
 * will be resolved to
 * d=1
 * a.b.c=1
 *
 * Source with properties
 * d.a=1
 * d.b.c=2
 * x=$link(d)
 *
 * will be resolved tp
 * d.a=1
 * d.b.c=2
 * x.a=1
 * x.b.c=2
 *
 */
public class LinkPropertiesResolver extends AbstractPropertiesResolver {

  private final Pattern linkPattern = Pattern.compile("\\$link\\(([^\\)]+)\\)");


  @Override
  protected void resolveProperty(String key, String value, Map<String, String> input, Map<String, String> output) {
    if (!containsPlaceholder(value)) {
      output.put(key, value);
      return;
    }
    String linkPrefix = extractLinkPrefixMatcher(key, value);
    putWithKeyFromPrefix(input, output, key, linkPrefix);
  }

  private void putWithKeyFromPrefix(Map<String, String> input, Map<String, String> output, String keyToReplace, String linkPrefix) {
    boolean found = false;
    for(Map.Entry<String,String> entry : input.entrySet()) {
      String key = entry.getKey();
      if(key.startsWith(linkPrefix)) {
        output.put(key.replace(linkPrefix, keyToReplace), entry.getValue());
        found = true;
      }
    }
    if (!found) {
      throw new IllegalStateException("Cannot find link values of "+ linkPrefix);
    }
  }

  private String extractLinkPrefixMatcher(String key, String value) {
    Matcher matcher = linkPattern.matcher(value);
    if (!matcher.find()) {
      throw new IllegalStateException("Invalid syntax found: incorrect link definition for key " + key);
    }
    return matcher.group(1);
  }

  private boolean containsPlaceholder(String value) {
    return value.startsWith("$link(");
  }
}
