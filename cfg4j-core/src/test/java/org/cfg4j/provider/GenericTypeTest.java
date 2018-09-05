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

package org.cfg4j.provider;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

import java.util.List;

class GenericTypeTest {

  @Test
  void throwsIfNotDirectlySubclassed() {
    abstract class GenericTypeDirect extends GenericType<List<Integer>> {
    }

    assertThatThrownBy(() -> new GenericTypeDirect() {
    }).isExactlyInstanceOf(IllegalArgumentException.class);
  }

  @Test
  void throwsIfNotParametrizedType() {
    assertThatThrownBy(() -> new GenericType() {
    }).isExactlyInstanceOf(IllegalArgumentException.class);
  }

  @Test
  void retainsType() {
    @SuppressWarnings("Convert2Diamond")
    GenericType<List<Integer>> genericType = new GenericType<List<Integer>>() {
    };

    assertThat(genericType.getType().toString()).isEqualTo("java.util.List<java.lang.Integer>");
  }
}