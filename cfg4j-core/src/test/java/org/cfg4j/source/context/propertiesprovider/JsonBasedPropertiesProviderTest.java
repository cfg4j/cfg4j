/*
 * Copyright 2015-2016 Norbert Potocki (norbert.potocki@nort.pl)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.cfg4j.source.context.propertiesprovider;

import static org.assertj.core.api.Assertions.assertThat;

import org.assertj.core.data.MapEntry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.InputStream;


class JsonBasedPropertiesProviderTest {




  private JsonBasedPropertiesProvider provider;

  @BeforeEach
  public void setUp() {
    provider = new JsonBasedPropertiesProvider();
  }

  @Test
  public void readsSingleValues() throws Exception {
    String path = "org/cfg4j/source/propertiesprovider/JsonBasedPropertiesProviderTest_readsSingleValues.json";

    try (InputStream input = getClass().getClassLoader().getResourceAsStream(path)) {
      assertThat(provider.getProperties(input)).containsExactly(MapEntry.entry("setting", "masterValue"),
          MapEntry.entry("integerSetting", 42));
    }
  }

  @Test
  public void readsNestedValues() throws Exception {
    String path = "org/cfg4j/source/propertiesprovider/JsonBasedPropertiesProviderTest_readsNestedValues.json";

    try (InputStream input = getClass().getClassLoader().getResourceAsStream(path)) {
      assertThat(provider.getProperties(input)).containsExactly(MapEntry.entry("some.setting", "masterValue"),
          MapEntry.entry("some.integerSetting", 42));
    }
  }

  @Test
  public void readsLists() throws Exception {
    String path = "org/cfg4j/source/propertiesprovider/JsonBasedPropertiesProviderTest_readsLists.json";

    try (InputStream input = getClass().getClassLoader().getResourceAsStream(path)) {
      assertThat(provider.getProperties(input)).containsOnly(MapEntry.entry("whitelist", "a,b,33"),
          MapEntry.entry("blacklist", "x,y,z"));
    }
  }

  @Test
  public void readsTextBlock() throws Exception {
    String path = "org/cfg4j/source/propertiesprovider/JsonBasedPropertiesProviderTest_readsTextBlock.json";

    try (InputStream input = getClass().getClassLoader().getResourceAsStream(path)) {
      assertThat(provider.getProperties(input)).containsExactly(MapEntry.entry("content", "I'm just a text block document"));
    }
  }

  @Test
  public void throwsForNonJsonFile() throws Exception {
    String path = "org/cfg4j/source/propertiesprovider/JsonBasedPropertiesProviderTest_throwsForNonJsonFile.json";

    try (InputStream input = getClass().getClassLoader().getResourceAsStream(path)) {
      // FIXME: expectedException.expect(IllegalStateException.class);
      provider.getProperties(input);
    }
  }

  @Test
  public void throwsOnNullInput() throws Exception {
    String path = "org/cfg4j/source/propertiesprovider/nonexistent.json";

    try (InputStream input = getClass().getClassLoader().getResourceAsStream(path)) {
      // FIXME: expectedException.expect(NullPointerException.class);
      provider.getProperties(input);
    }
  }
}