package org.cfg4j.source.resolve;


import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;

public class ExtendesPropertiesResolverTest extends AbstractPropertiesResolverTest {

  @Before
  public void init() {
    resolver = new ExtendsPropertiesResolver();
  }

  @Test
  public void resolveExtendsToMultipleProperties() throws Exception {
    testResolve(asMap(
      "a.^extends", "l.a",
      "l.a", "1",
      "l.a.b", "2",
      "l.a.c.d", "3"
      ),
      asMap(
        "l.a", "1",
        "l.a.b", "2",
        "l.a.c.d", "3",
        "a", "1",
        "a.b", "2",
        "a.c.d", "3"));
  }

  @Test(expected = IllegalStateException.class)
  public void resolveUnexistedLink() throws Exception {
    testResolve(asMap(
      "a.^extends", "unexisted"),
      null);
  }

  @Test
  public void resolveWithOwerride() throws Exception {
    String overriden = "22";
    testResolve(asMap(
      "a.^extends", "l.a",
      "a.b", overriden,
      "l.a", "1",
      "l.a.b", "2",
      "l.a.c.d", "3"
      ),
      asMap(
        "l.a", "1",
        "l.a.b", "2",
        "l.a.c.d", "3",
        "a", "1",
        "a.b", overriden,
        "a.c.d", "3"));

    testResolve(asMap(
      "a.^extends", "l.a",
      "l.a", "1",
      "l.a.b", "2",
      "l.a.c.d", "3",
      "a.b", overriden
      ),
      asMap(
        "l.a", "1",
        "l.a.b", "2",
        "l.a.c.d", "3",
        "a", "1",
        "a.b", overriden,
        "a.c.d", "3"));

    testResolve(asMap(
      "a.b", overriden,
      "a.^extends", "l.a",
      "l.a", "1",
      "l.a.b", "2",
      "l.a.c.d", "3"
      ),
      asMap(
        "l.a", "1",
        "l.a.b", "2",
        "l.a.c.d", "3",
        "a", "1",
        "a.b", overriden,
        "a.c.d", "3"));  }
}