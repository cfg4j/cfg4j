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
public class PropertyBasedPropertiesProviderTest {

  @Rule
  public ExpectedException expectedException = ExpectedException.none();

  private PropertyBasedPropertiesProvider provider;

  @Before
  public void setUp() throws Exception {
    provider = new PropertyBasedPropertiesProvider();
  }

  @Test
  public void shouldLoadProperties() throws Exception {
    String path = "org/cfg4j/source/propertiesprovider/PropertyBasedPropertiesProviderTest_shouldLoadProperties.properties";

    try (InputStream input = getClass().getClassLoader().getResourceAsStream(path)) {
      assertThat(provider.getProperties(input)).containsOnly(MapEntry.entry("property", "abc"));
    }
  }

  @Test
  public void shouldThrowOnMalformedFile() throws Exception {
    String path = "org/cfg4j/source/propertiesprovider/PropertyBasedPropertiesProviderTest_shouldThrowOnMalformedFile.properties";

    try (InputStream input = getClass().getClassLoader().getResourceAsStream(path)) {
      expectedException.expect(IllegalStateException.class);
      provider.getProperties(input);
    }
  }
}