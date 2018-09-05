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
package org.cfg4j.source.git;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import org.cfg4j.source.context.environment.Environment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class FirstTokenBranchResolverTest {

  @Mock
  private Environment environment;

  private FirstTokenBranchResolver branchResolver;

  @BeforeEach
  void setUp() {
    branchResolver = new FirstTokenBranchResolver();
  }

  @Test
  void resolvesEmptyStringToMaster() {
    when(environment.getName()).thenReturn("");

    assertThat(branchResolver.getBranchNameFor(environment)).isEqualTo("master");
  }

  @Test
  void resolvesWhitespacesToMaster() {
    when(environment.getName()).thenReturn("   ");

    assertThat(branchResolver.getBranchNameFor(environment)).isEqualTo("master");
  }

  @Test
  void supportsSingleToken() {
    when(environment.getName()).thenReturn("us-west-1");

    assertThat(branchResolver.getBranchNameFor(environment)).isEqualTo("us-west-1");
  }

  @Test
  void usesFirstTokenAsBranchName() {
    when(environment.getName()).thenReturn("us-west-1/local/path");

    assertThat(branchResolver.getBranchNameFor(environment)).isEqualTo("us-west-1");
  }
}