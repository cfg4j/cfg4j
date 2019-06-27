package org.cfg4j.source.resolve;


import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;

public class TaggedPropertiesResolverTest extends AbstractPropertiesResolverTest {

  @Before
  public void init() {
    HashSet<String> tags = new HashSet<>(Arrays.asList("foo", "bar"));
    resolver = new TaggedPropertiesResolver('$', tags);
  }

  @Test
  public void resolveOfFoundTag() throws Exception {
    testResolve(asMap(
        "$foo.a", "1",
        "a.$bar.b", "2",
        "a.b.c.$foo", "3"),
      asMap(
        "a", "1",
        "a.b", "2",
        "a.b.c", "3"));
  }

  @Test
  public void passUnTaggedPropertiesAsIs() throws Exception {
    testResolve(asMap(
        "a.b", "1"),
        asMap(
            "a.b", "1"));
  }

  @Test
  public void ignoreNotFoundTags() throws Exception {
    testResolve(asMap(
        "$baz.a", "1",
        "a.$baz.b", "2",
        "a.b.c.$baz", "3"),
      Collections.<String, Object>emptyMap());
  }

  @Test
  public void resolveMultipleTags() throws Exception {
    testResolve(asMap(
        "a.$foo.$bar", "1",
        "a.$foo.b.$bar", "2",
        "$bar.a.b.c.$foo", "3",
        "$bar.a.b.c.$foo.d", "4"),
      asMap(
        "a", "1",
        "a.b", "2",
        "a.b.c", "3",
        "a.b.c.d", "4"));
  }

  @Test
  public void resolveMultipleTagsWithPriorityToMoreSpecificMatch() throws Exception {
    testResolve(asMap(
      "$foo.$bar.a", "1",
      "$foo.a", "2"),

      asMap(
        "a", "1"
        ));
  }

  @Test(expected = IllegalStateException.class)
  public void failOnAmbiguousMatch() throws Exception {
    resolver.resolve(asMap(
      "$foo.a", "1",
      "$bar.a", "2"));
  }


  @Test
  public void ignoreIfOneOfTagsDoesntMatchPreConfiguredTags() throws Exception {
    testResolve(asMap(
        "a.$baz.$bar", "1",
        "a.$baz.b.$bar", "2",
        "$bar.a.b.c.$baz", "3",
        "$baz.a.b.c.$foo.d", "4"), Collections.<String, Object>emptyMap()
      );
  }


}