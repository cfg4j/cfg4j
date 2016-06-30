package org.cfg4j.source.resolve;


import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;

public class LinkPropertiesResolverTest extends AbstractPropertiesResolverTest {

  @Before
  public void init() {
    resolver = new LinkPropertiesResolver();
  }

  @Test
  public void resolveLinkToSingleProperty() throws Exception {
    testResolve(asMap(
        "a", "$link(l.a)",
        "l.a", "1",
        "l.b", "2",
        "a.b", "$link(l.b)"),
      asMap(
        "l.a", "1",
        "l.b", "2",
        "a", "1",
        "a.b", "2"));
  }

  @Test
  public void resolveLinkToMultipleProperties() throws Exception {
    testResolve(asMap(
        "a", "$link(l.a)",
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
        "a", "$link(unexisted)"),
      null);
  }

  @Test(expected = IllegalStateException.class)
  public void resolveInvalidLink() throws Exception {
    testResolve(asMap(
        "a", "$link(invalid"),
      null);
  }

}