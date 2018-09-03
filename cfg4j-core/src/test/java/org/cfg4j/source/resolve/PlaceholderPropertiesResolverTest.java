package org.cfg4j.source.resolve;


import org.junit.Before;
import org.junit.Test;

import java.util.Collections;

public class PlaceholderPropertiesResolverTest extends AbstractPropertiesResolverTest {

  @Before
  public void init() {
    System.setProperty("env", "-test-");
    resolver = new PlaceholderPropertiesResolver();
  }

  @Test
  public void resolveOfFoundSystemProperty() throws Exception {
    testResolve(asMap(
        "a", "${env}",
        "a.b", "x${env}",
        "a.b.c", "${env}x",
        "a.b.c.d", "x${env}x"),
      asMap(
        "a", "-test-",
        "a.b", "x-test-",
        "a.b.c", "-test-x",
        "a.b.c.d", "x-test-x"));
  }

  @Test(expected = IllegalStateException.class)
  public void resolveOfNotFoundSystemProperty() throws Exception {
    testResolve(asMap(
        "a", "${xxx}"
        ),
      Collections.<String, Object>emptyMap());
  }

  @Test
  public void resolveIfNoPlaceholder() throws Exception {
    testResolve(asMap(
        "a", "1",
        "a.b", "2"),

      asMap(
        "a", "1",
        "a.b", "2"));
  }

  @Test
  public void resolveFromPropertiesInSource() throws Exception {
    testResolve(asMap(
      "a", "1",
      "a.b", "${a}"),

      asMap(
        "a", "1",
        "a.b", "1"));
  }

  @Test
  public void resolveWithDigitsFromPropertiesInSource() throws Exception {
    testResolve(asMap(
      "a1.A2", "1",
      "a.b", "${a1.A2}"),

      asMap(
        "a.b", "1",
        "a1.A2", "1"));
  }


  @Test
  public void reccursiveResolve() throws Exception {
    testResolve(asMap(
      "a", "1",
      "a.b", "${a}",
      "a.b.c", "${a.b}"),

      asMap(
        "a", "1",
        "a.b", "1",
        "a.b.c", "1"));
  }

  @Test
  public void multiplePlaceholdersInOneValue() throws Exception {
    testResolve(asMap(
      "a", "1",
      "b", "2",
      "c", "${a}+${b}"),

      asMap(
        "a", "1",
        "b", "2",
        "c", "1+2"));
  }



}