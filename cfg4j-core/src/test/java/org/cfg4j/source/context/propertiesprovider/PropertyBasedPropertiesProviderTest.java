/*
 * Copyright 2015-2018 Norbert Potocki (norbert.potocki@nort.pl)
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
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.assertj.core.data.MapEntry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.InputStream;


class PropertyBasedPropertiesProviderTest {

  private PropertyBasedPropertiesProvider provider;

  @BeforeEach
  void setUp() {
    provider = new PropertyBasedPropertiesProvider();
  }

  @Test
  void loadsProperties() throws Exception {
    String path = "org/cfg4j/source/propertiesprovider/PropertyBasedPropertiesProviderTest_loadsProperties.properties";

    try (InputStream input = getClass().getClassLoader().getResourceAsStream(path)) {
      assertThat(provider.getProperties(input)).containsExactly(MapEntry.entry("property", "abc"));
    }
  }

  @Test
  void throwsOnMalformedFile() throws Exception {
    String path = "org/cfg4j/source/propertiesprovider/PropertyBasedPropertiesProviderTest_throwsOnMalformedFile.properties";

    try (InputStream input = getClass().getClassLoader().getResourceAsStream(path)) {
      assertThatThrownBy(() -> provider.getProperties(input)).isExactlyInstanceOf(IllegalStateException.class);
    }
  }
}