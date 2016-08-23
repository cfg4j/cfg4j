package org.cfg4j.source.resolve;

import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Property resolver that process tags in keys. Implements {@link PropertiesResolver}.
 * Tags can be used as selector for properties. We can get different sets of properties depends on set of tags provided in constructor.
 *
 * Source with properties
 *
 * a.$dev1.b=1
 * $dev1.c=2
 * $dev2.c=22
 * $dev1.d.$us=3
 * $dev1.d.$eu=33
 *
 * where tags=["dev1", "us"]
 * will be resolved to
 *
 * a.b=1
 * c=2
 * d=3
 */
public class TaggedPropertiesResolver extends AbstractPropertiesResolver {
  private final char tagSymbol;
  private final Set<String> tags;
  private final Pattern tagPattern;

  /**
   * Constructor
   * @param tagSymbol - by default is equal to $. This symbol used to find tags in keys.
   * @param tags - set of tags that will be used for filtering
   */
  public TaggedPropertiesResolver(char tagSymbol, Set<String> tags) {
    this.tagSymbol = tagSymbol;
    this.tags = tags;
    tagPattern = Pattern.compile("(^|\\.)\\" + tagSymbol + "([^.]+)");
  }

  /**
   * @param tags - set of tags that will be used for filtering
   */
  public TaggedPropertiesResolver(Set<String> tags) {
    this('$', tags);
  }

  @Override
  protected void resolveProperty(String key, Object value, Map<String, Object> input, Map<String, Object> output) {
    if (isTagged(key)) {
      if (containsTags(key)) {
        output.put(cleanupKey(key), value);
      }
    } else {
      output.put(key, value);
    }
  }

  private boolean containsTags(String key) {
    Matcher matcher = tagPattern.matcher(key);
    boolean allFoundTagsMathed = false;
    while (matcher.find()) {
      String foundTag = matcher.group(2);
      if (tags.contains(foundTag)) {
        allFoundTagsMathed = true;
      } else {
        return false;
      }
    }
    return allFoundTagsMathed;
  }

  private String cleanupKey(String key) {
    String[] words = key.split("\\.");
    StringBuilder result = new StringBuilder();
    boolean isFirst = true;
    for (String word : words) {

      if (word.charAt(0) != tagSymbol) {
        if (isFirst) {
          isFirst = false;
          result.append(word);
        } else

          result.append('.').append(word);
      }
    }
    return result.toString();
  }

  private boolean isTagged(String key) {
    return key.indexOf(tagSymbol) != -1;
  }
}
