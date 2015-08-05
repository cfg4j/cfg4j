/*
 * Copyright 2015 Norbert Potocki (norbert.potocki@nort.pl)
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

package org.cfg4j.utils;

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
public class YamlBasedPropertiesProviderTest {

  @Rule
  public ExpectedException expectedException = ExpectedException.none();

  private YamlBasedPropertiesProvider provider;

  @Before
  public void setUp() throws Exception {
    provider = new YamlBasedPropertiesProvider();
  }

  @Test
  public void shouldReadSingleValues() throws Exception {
    String path = "org/cfg4j/utils/YamlBasedPropertiesProviderTest_shouldReadSingleValues.yaml";

    try (InputStream input = getClass().getClassLoader().getResourceAsStream(path)) {
      assertThat(provider.getProperties(input)).containsExactly(MapEntry.entry("setting", "masterValue"),
          MapEntry.entry("integerSetting", 42));
    }
  }

  @Test
  public void shouldReadNestedValues() throws Exception {
    String path = "org/cfg4j/utils/YamlBasedPropertiesProviderTest_shouldReadNestedValues.yaml";

    try (InputStream input = getClass().getClassLoader().getResourceAsStream(path)) {
      assertThat(provider.getProperties(input)).containsExactly(MapEntry.entry("some.setting", "masterValue"),
          MapEntry.entry("some.integerSetting", 42));
    }
  }

  @Test
  public void shouldReadLists() throws Exception {
    String path = "org/cfg4j/utils/YamlBasedPropertiesProviderTest_shouldReadLists.yaml";

    try (InputStream input = getClass().getClassLoader().getResourceAsStream(path)) {
      assertThat(provider.getProperties(input)).containsOnly(MapEntry.entry("whitelist", "a,b,33"),
          MapEntry.entry("blacklist", "x,y,z"));
    }
  }

  @Test
  public void shouldReadTextBlock() throws Exception {
    String path = "org/cfg4j/utils/YamlBasedPropertiesProviderTest_shouldReadTextBlock.yaml";

    try (InputStream input = getClass().getClassLoader().getResourceAsStream(path)) {
      assertThat(provider.getProperties(input)).containsExactly(MapEntry.entry("content", "I'm just a text block document"));
    }
  }

  @Test
  public void shouldSupportReferences() throws Exception {
    String path = "org/cfg4j/utils/YamlBasedPropertiesProviderTest_shouldSupportReferences.yaml";

    try (InputStream input = getClass().getClassLoader().getResourceAsStream(path)) {
      assertThat(provider.getProperties(input)).containsOnly(
          MapEntry.entry("wheelA.radius", "25cm"), MapEntry.entry("wheelA.color", "black"),
          MapEntry.entry("wheelB.radius", "25cm"), MapEntry.entry("wheelB.color", "black")
      );
    }
  }

  @Test
  public void shouldThrowForNonYamlFile() throws Exception {
    String path = "org/cfg4j/utils/YamlBasedPropertiesProviderTest_shouldThrowForNonYamlFile.yaml";

    try (InputStream input = getClass().getClassLoader().getResourceAsStream(path)) {
      expectedException.expect(IllegalStateException.class);
      provider.getProperties(input);
    }

  }
}