package org.cfg4j.source.resolve;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Property resolver that process tags in keys. Implements {@link PropertiesResolver}. Tags can be used as selector for
 * properties. We can get different sets of properties depends on set of tags provided in constructor.
 *
 * Source with properties
 *
 * a.$dev1.b=1 $dev1.c=2 $dev2.c=22 $dev1.d.$us=3 $dev1.d.$eu=33
 *
 * where tags=["dev1", "us"] will be resolved to
 *
 * a.b=1 c=2 d=3
 */
public class TaggedPropertiesResolver implements PropertiesResolver {

  private final char tagSymbol;
  private final Pattern tagPattern;
  private Map<Integer, List<List<String>>> tagPremutationsByLevel;

  /**
   * Constructor
   *
   * @param tagSymbol - by default is equal to $. This symbol used to find tags in keys.
   * @param tags - set of tags that will be used for filtering
   */
  public TaggedPropertiesResolver(char tagSymbol, Set<String> tags) {
    this.tagSymbol = tagSymbol;
    tagPattern = Pattern.compile(String.format("(^|\\.)\\%s([^.]+)", tagSymbol));
    tagPremutationsByLevel = tagPremutationsByLevel(tags);
  }

  private Map<Integer, List<List<String>>> tagPremutationsByLevel(Set<String> tags) {
    Set<List<String>> result = tagCombinations(tags);
    return new TreeMap<>(result.stream()
        .collect(Collectors.groupingBy(List::size)));
  }

  @Override
  public Map<String, Object> resolve(Map<String, Object> sourceProperties) {
    Map<String, Object> result = new HashMap<>();
    sourceProperties.entrySet().stream()
        .filter(e -> !isTagged(e.getKey()))//
        .forEach(e -> result.put(e.getKey(), e.getValue()));

    List<Entry<String, Object>> taggedEntries = sourceProperties.entrySet().stream()
        .filter(e -> isTagged(e.getKey()))//
        .collect(Collectors.toList());

    tagPremutationsByLevel.entrySet().stream()
        .map(Entry::getValue)
        .forEach(leveledTags -> {
          Map<String, Object> levelResult = new HashMap<>();
          leveledTags.forEach(tags -> {
            List<Tupple> resolvedTupples = taggedEntries.stream()
                .map(e -> resolveWithTags(e, tags))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
            resolvedTupples.stream().forEach(tupple -> {
              if (levelResult.containsKey(tupple.key)) {
                throw new IllegalStateException(String.format(
                    "Ambigouse key %s found during resolve tags %s. Same key set during processing one of tag set:%s",
                    tupple.key, tags, leveledTags));
              }
              levelResult.put(tupple.key, tupple.value);
            });
          });
          result.putAll(levelResult);
        });

    return result;
  }

  private Tupple resolveWithTags(Map.Entry<String, Object> entry, List<String> tags) {
    List<String> foundTags = findTags(entry.getKey());
    if (tags.size() == foundTags.size() && foundTags.containsAll(tags)) {
      return new Tupple(cleanupKey(entry.getKey()), entry.getValue());
    }
    return null;
  }

  /**
   * @param tags - set of tags that will be used for filtering
   */
  public TaggedPropertiesResolver(Set<String> tags) {
    this('$', tags);
  }

  private List<String> findTags(String key) {
    Matcher matcher = tagPattern.matcher(key);
    List<String> result = new ArrayList<>();
    while (matcher.find()) {
      result.add(matcher.group(2));
    }
    return result;
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
        } else {
          result.append('.').append(word);
        }
      }
    }
    return result.toString();
  }

  private boolean isTagged(String key) {
    return key.indexOf(tagSymbol) != -1;
  }

  class Tupple {

    final String key;
    final Object value;

    Tupple(String key, Object value) {
      this.key = key;
      this.value = value;
    }

    public String getKey() {
      return key;
    }

    public Object getValue() {
      return value;
    }
  }

  private Set<List<String>> tagCombinations(Set<String> tags) {
    Set<List<String>> result = new HashSet<>();
    permutation(0, tags.toArray(new String[0]), result);
    return result;
  }

  private void swap(int pos1, int pos2, String[] c) {
    String temp = c[pos1];
    c[pos1] = c[pos2];
    c[pos2] = temp;
  }

  private void permutation(int start, String[] c, Set<List<String>> result) {
    if (start != 0) {
      List<String> premutationResult = new ArrayList<>();
      for (int i = 0; i < start; i++) {
        premutationResult.add(c[i]);
      }
      premutationResult.sort(Comparator.naturalOrder());
      result.add(premutationResult);
    }

    for (int i = start; i < c.length; i++) {
      swap(start, i, c);
      permutation(start + 1, c, result);
      swap(start, i, c);
    }
  }
}


