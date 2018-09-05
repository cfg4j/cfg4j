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

package org.cfg4j.provider;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import java.util.List;

class GenericTypeTest {




  @Test
  public void throwsIfNotDirectlySubclassed() throws Exception {
    abstract class GenericTypeDirect extends GenericType<List<Integer>> {
    }

    // FIXME: expectedException.expect(IllegalArgumentException.class);
    new GenericTypeDirect() {
    };
  }

  @Test
  public void throwsIfNotParametrizedType() throws Exception {
    // FIXME: expectedException.expect(IllegalArgumentException.class);
    new GenericType() {
    };
  }

  @Test
  public void retainsType() throws Exception {
    GenericType<List<Integer>> genericType = new GenericType<>() {
    };

    assertThat(genericType.getType().toString()).isEqualTo("java.util.List<java.lang.Integer>");
  }
}