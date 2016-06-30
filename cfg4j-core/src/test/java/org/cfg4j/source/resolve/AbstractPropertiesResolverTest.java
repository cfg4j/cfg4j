package org.cfg4j.source.resolve;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class AbstractPropertiesResolverTest {
  protected PropertiesResolver resolver;

  protected void testResolve(Map<String, String> source, Map<String, String> expexted) {
    assertThat(resolver.resolve(source)).isEqualTo(expexted);
  }

  protected Map<String, String> asMap(String... args) {
    Map<String, String> result = new HashMap<>();
    for (int i = 1; i < args.length; i += 2) {
      result.put(args[i - 1], args[i]);
    }
    return result;
  }
}
