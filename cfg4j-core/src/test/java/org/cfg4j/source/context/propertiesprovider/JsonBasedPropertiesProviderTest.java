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
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.InputStream;


@RunWith(MockitoJUnitRunner.class)
public class JsonBasedPropertiesProviderTest {

  @Rule
  public ExpectedException expectedException = ExpectedException.none();

  private JsonBasedPropertiesProvider provider;

  @Before
  public void setUp() throws Exception {
    provider = new JsonBasedPropertiesProvider();
  }

  @Test
  public void shouldReadSingleValues() throws Exception {
    String path = "org/cfg4j/source/propertiesprovider/JsonBasedPropertiesProviderTest_shouldReadSingleValues.json";

    try (InputStream input = getClass().getClassLoader().getResourceAsStream(path)) {
      assertThat(provider.getProperties(input)).containsExactly(MapEntry.entry("setting", "masterValue"),
          MapEntry.entry("integerSetting", 42));
    }
  }

  @Test
  public void shouldReadNestedValues() throws Exception {
    String path = "org/cfg4j/source/propertiesprovider/JsonBasedPropertiesProviderTest_shouldReadNestedValues.json";

    try (InputStream input = getClass().getClassLoader().getResourceAsStream(path)) {
      assertThat(provider.getProperties(input)).containsExactly(MapEntry.entry("some.setting", "masterValue"),
          MapEntry.entry("some.integerSetting", 42));
    }
  }

  @Test
  public void shouldReadLists() throws Exception {
    String path = "org/cfg4j/source/propertiesprovider/JsonBasedPropertiesProviderTest_shouldReadLists.json";

    try (InputStream input = getClass().getClassLoader().getResourceAsStream(path)) {
      assertThat(provider.getProperties(input)).containsOnly(MapEntry.entry("whitelist", "a,b,33"),
          MapEntry.entry("blacklist", "x,y,z"));
    }
  }

  @Test
  public void shouldReadTextBlock() throws Exception {
    String path = "org/cfg4j/source/propertiesprovider/JsonBasedPropertiesProviderTest_shouldReadTextBlock.json";

    try (InputStream input = getClass().getClassLoader().getResourceAsStream(path)) {
      assertThat(provider.getProperties(input)).containsExactly(MapEntry.entry("content", "I'm just a text block document"));
    }
  }

  @Test
  public void shouldThrowForNonJsonFile() throws Exception {
    String path = "org/cfg4j/source/propertiesprovider/JsonBasedPropertiesProviderTest_shouldThrowForNonYamlFile.json";

    try (InputStream input = getClass().getClassLoader().getResourceAsStream(path)) {
      expectedException.expect(IllegalStateException.class);
      provider.getProperties(input);
    }
  }
}