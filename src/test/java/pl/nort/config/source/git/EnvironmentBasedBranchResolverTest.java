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
package pl.nort.config.source.git;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import pl.nort.config.source.context.Environment;


@RunWith(MockitoJUnitRunner.class)
public class EnvironmentBasedBranchResolverTest {

  @Rule
  public ExpectedException expectedException = ExpectedException.none();

  @Mock
  private Environment environment;

  private EnvironmentBasedBranchResolver branchResolver;

  @Before
  public void setUp() throws Exception {
    branchResolver = new EnvironmentBasedBranchResolver();
  }

  @Test
  public void shouldResolveEmptyStringToMaster() throws Exception {
    when(environment.getName()).thenReturn("");

    assertThat(branchResolver.getBranchNameFor(environment)).isEqualTo("master");
  }

  @Test
  public void shouldResolveWhitespacesToMaster() throws Exception {
    when(environment.getName()).thenReturn("   ");

    assertThat(branchResolver.getBranchNameFor(environment)).isEqualTo("master");
  }

  @Test
  public void shouldSupportSingleToken() throws Exception {
    when(environment.getName()).thenReturn("us-west-1");

    assertThat(branchResolver.getBranchNameFor(environment)).isEqualTo("us-west-1");
  }

  @Test
  public void shouldUseFirstTokenAsBranchName() throws Exception {
    when(environment.getName()).thenReturn("us-west-1/local/path");

    assertThat(branchResolver.getBranchNameFor(environment)).isEqualTo("us-west-1");
  }
}