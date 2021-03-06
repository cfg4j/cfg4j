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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


@ExtendWith(MockitoExtension.class)
class PropertiesProviderSelectorTest {

  @Mock
  private PropertiesProvider yamlProvider;

  @Mock
  private PropertiesProvider jsonProvider;

  @Mock
  private PropertiesProvider propertiesProvider;

  private PropertiesProviderSelector selector;

  @BeforeEach
  void setUp() {
    selector = new PropertiesProviderSelector(propertiesProvider, yamlProvider, jsonProvider);
  }

  @Test
  void returnsYamlProviderForYaml() {
    assertThat(selector.getProvider("test.yaml")).isEqualTo(yamlProvider);
  }

  @Test
  void returnsYamlProviderForYml() {
    assertThat(selector.getProvider("test.yml")).isEqualTo(yamlProvider);
  }

  @Test
  void returnsJsonProviderForJson() {
    assertThat(selector.getProvider("test.json")).isEqualTo(jsonProvider);
  }

  @Test
  void returnsPropertiesProviderForNonYaml() {
    assertThat(selector.getProvider("test.properties")).isEqualTo(propertiesProvider);
  }
}