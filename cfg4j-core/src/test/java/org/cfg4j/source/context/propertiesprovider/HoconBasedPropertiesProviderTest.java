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