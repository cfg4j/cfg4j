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

package pl.nort.config.provider;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.List;

public class GenericTypeTest {

  @Rule
  public ExpectedException expectedException = ExpectedException.none();

  @Test
  public void shouldThrowIfNotDirectlySubclassed() throws Exception {
    abstract class GenericTypeDirect extends GenericType<List<Integer>> {
    }

    expectedException.expect(IllegalArgumentException.class);
    new GenericTypeDirect() {
    };
  }

  @Test
  public void shouldThrowIfNotParametrizedType() throws Exception {
    expectedException.expect(IllegalArgumentException.class);
    new GenericType() {
    };
  }

  @Test
  public void shouldRetainType() throws Exception {
    GenericType<List<Integer>> genericType = new GenericType<List<Integer>>() {
    };

    assertThat(genericType.getType().getTypeName()).isEqualTo("java.util.List<java.lang.Integer>");
  }
}