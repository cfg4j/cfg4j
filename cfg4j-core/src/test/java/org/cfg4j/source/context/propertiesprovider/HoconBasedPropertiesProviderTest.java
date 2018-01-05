package org.cfg4j.source.context.propertiesprovider;

import org.assertj.core.data.MapEntry;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.IOException;
import java.io.InputStream;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class HoconBasedPropertiesProviderTest {

  @Rule
  public ExpectedException expectedException = ExpectedException.none();

  private HoconBasedPropertiesProvider provider;

  @Before
  public void setUp() throws Exception {
    provider = new HoconBasedPropertiesProvider();
  }

  @Test
  public void readsNestedValues() throws IOException {
    String path = "org/cfg4j/source/propertiesprovider/HoconBasedPropertiesProviderTest_readsNestedValues.conf";

    try (InputStream input = getClass().getClassLoader().getResourceAsStream(path)) {
      assertThat(provider.getProperties(input)).containsExactly(
        MapEntry.entry("some.listSetting", "stringValue,102"),
        MapEntry.entry("some.setting", "masterValue"),
        MapEntry.entry("some.nestedSetting.integerSetting", 123)
      );
    }
  }

  @Test
  public void throwsOnNullInput() throws IOException {
    String path = "org/cfg4j/source/propertiesprovider/nonexistent.json";

    try (InputStream input = getClass().getClassLoader().getResourceAsStream(path)) {
      expectedException.expect(NullPointerException.class);
      provider.getProperties(input);
    }
  }
}