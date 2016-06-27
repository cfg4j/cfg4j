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
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;


@RunWith(MockitoJUnitRunner.class)
public class BindInvocationHandlerTest {

  @Rule
  public ExpectedException expectedException = ExpectedException.none();

  @Mock
  private ConfigurationProvider configurationProvider;

  @Captor
  public ArgumentCaptor<GenericTypeInterface> captor;

  @Test
  public void shouldUseProvidedPrefix() throws Exception {
    BindInvocationHandler handler = new BindInvocationHandler(configurationProvider, "abc");

    handler.invoke(this, this.getClass().getMethod("stringMethod"), new Object[]{});

    verify(configurationProvider, times(1)).getProperty(eq("abc.stringMethod"), any(GenericTypeInterface.class));
  }

  @Test
  public void shouldUseDefaultNamespaceWhenNoPrefix() throws Exception {
    BindInvocationHandler handler = new BindInvocationHandler(configurationProvider, "");

    handler.invoke(this, this.getClass().getMethod("stringMethod"), new Object[]{});

    verify(configurationProvider, times(1)).getProperty(eq("stringMethod"), any(GenericTypeInterface.class));
  }

  @Test
  public void shouldQueryForProvidedType() throws Exception {
    BindInvocationHandler handler = new BindInvocationHandler(configurationProvider, "");

    handler.invoke(this, this.getClass().getMethod("mapMethod"), new Object[]{});

    verify(configurationProvider, times(1)).getProperty(eq("mapMethod"), captor.capture());
    assertThat(captor.getValue().getType().toString()).isEqualTo("java.util.Map<java.util.List<java.lang.Integer>, java.lang.Boolean>");
  }

  @Test
  public void shouldPropagateNoSuchElementException() throws Exception {
    when(configurationProvider.getProperty(anyString(), any(GenericTypeInterface.class))).thenThrow(new NoSuchElementException());
    BindInvocationHandler handler = new BindInvocationHandler(configurationProvider, "");

    expectedException.expect(NoSuchElementException.class);
    handler.invoke(this, this.getClass().getMethod("stringMethod"), new Object[]{});
  }

  @Test
  public void shouldPropagateIllegalArgumentException() throws Exception {
    when(configurationProvider.getProperty(anyString(), any(GenericTypeInterface.class))).thenThrow(new IllegalArgumentException());
    BindInvocationHandler handler = new BindInvocationHandler(configurationProvider, "");

    expectedException.expect(IllegalArgumentException.class);
    handler.invoke(this, this.getClass().getMethod("stringMethod"), new Object[]{});
  }

  @Test
  public void shouldPropagateIllegalStateException() throws Exception {
    when(configurationProvider.getProperty(anyString(), any(GenericTypeInterface.class))).thenThrow(new IllegalStateException());
    BindInvocationHandler handler = new BindInvocationHandler(configurationProvider, "");

    expectedException.expect(IllegalStateException.class);
    handler.invoke(this, this.getClass().getMethod("stringMethod"), new Object[]{});
  }

  @Test
  public void shouldPassCallToNonObjectLevelMethodWithCollidingName() throws Exception {
    when(configurationProvider.getProperty(eq("equals"), any(GenericTypeInterface.class))).thenReturn(true);
    BindInvocationHandler handler = new BindInvocationHandler(configurationProvider, "");

    assertThat((boolean) handler.invoke(this, this.getClass().getMethod("equals", String.class), new Object[]{})).isTrue();
  }

  @Test
  public void shouldPassCallToNonObjectLevelMethodWithCollidingNameAndDifferentNumberOfParams() throws Exception {
    when(configurationProvider.getProperty(eq("equals"), any(GenericTypeInterface.class))).thenReturn(true);
    BindInvocationHandler handler = new BindInvocationHandler(configurationProvider, "");

    assertThat((boolean) handler.invoke(this, this.getClass().getMethod("equals", String.class, String.class), new Object[]{})).isTrue();
  }

  @Test
  public void shouldInvokeObjectLevelMethod() throws Exception {
    when(configurationProvider.getProperty(eq("hashCode"), any(GenericTypeInterface.class))).thenThrow(new NoSuchElementException());
    BindInvocationHandler handler = new BindInvocationHandler(configurationProvider, "");

    int hashCode = (int) handler.invoke(this, this.getClass().getMethod("hashCode"), new Object[]{});
    assertThat(hashCode).isEqualTo(handler.hashCode());
  }

  // For "mocking" java.lang.reflect.Method
  public String stringMethod() {
    return null;
  }

  public Map<List<Integer>, Boolean> mapMethod() {
    return null;
  }

  // Name collision with {@link Object#equals(Object)} (but with different parameters)
  public boolean equals(String param) {
    return true;
  }

  public boolean equals(String param1, String param2) {
    return true;
  }
}