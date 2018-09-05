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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;


@ExtendWith(MockitoExtension.class)
class BindInvocationHandlerTest {

  @Mock
  private ConfigurationProvider configurationProvider;

  @Captor
  public ArgumentCaptor<GenericTypeInterface> captor;

  @Test
  void usesProvidedPrefix() throws Exception {
    BindInvocationHandler handler = new BindInvocationHandler(configurationProvider, "abc");

    handler.invoke(this, this.getClass().getMethod("stringMethod"), new Object[]{});

    verify(configurationProvider, times(1)).getProperty(eq("abc.stringMethod"), any(GenericTypeInterface.class));
  }

  @Test
  void usesDefaultNamespaceWhenNoPrefix() throws Exception {
    BindInvocationHandler handler = new BindInvocationHandler(configurationProvider, "");

    handler.invoke(this, this.getClass().getMethod("stringMethod"), new Object[]{});

    verify(configurationProvider, times(1)).getProperty(eq("stringMethod"), any(GenericTypeInterface.class));
  }

  @Test
  void queriesForProvidedType() throws Exception {
    BindInvocationHandler handler = new BindInvocationHandler(configurationProvider, "");

    handler.invoke(this, this.getClass().getMethod("mapMethod"), new Object[]{});

    verify(configurationProvider, times(1)).getProperty(eq("mapMethod"), captor.capture());
    assertThat(captor.getValue().getType().toString()).isEqualTo("java.util.Map<java.util.List<java.lang.Integer>, java.lang.Boolean>");
  }

  @Test
  void propagatesNoSuchElementException() throws Exception {
    when(configurationProvider.getProperty(anyString(), any(GenericTypeInterface.class))).thenThrow(new NoSuchElementException());
    BindInvocationHandler handler = new BindInvocationHandler(configurationProvider, "");

    assertThatThrownBy(() -> handler.invoke(this, this.getClass().getMethod("stringMethod"), new Object[]{})).isExactlyInstanceOf(NoSuchElementException.class);
  }

  @Test
  void propagatesIllegalArgumentException() throws Exception {
    when(configurationProvider.getProperty(anyString(), any(GenericTypeInterface.class))).thenThrow(new IllegalArgumentException());
    BindInvocationHandler handler = new BindInvocationHandler(configurationProvider, "");

    assertThatThrownBy(() -> handler.invoke(this, this.getClass().getMethod("stringMethod"), new Object[]{})).isExactlyInstanceOf(IllegalArgumentException.class);
  }

  @Test
  void propagatesIllegalStateException() throws Exception {
    when(configurationProvider.getProperty(anyString(), any(GenericTypeInterface.class))).thenThrow(new IllegalStateException());
    BindInvocationHandler handler = new BindInvocationHandler(configurationProvider, "");

    assertThatThrownBy(() -> handler.invoke(this, this.getClass().getMethod("stringMethod"), new Object[]{})).isExactlyInstanceOf(IllegalStateException.class);
  }

  @Test
  void passesCallToNonObjectLevelMethodWithCollidingName() throws Exception {
    when(configurationProvider.getProperty(eq("equals"), any(GenericTypeInterface.class))).thenReturn(true);
    BindInvocationHandler handler = new BindInvocationHandler(configurationProvider, "");

    assertThat((boolean) handler.invoke(this, this.getClass().getMethod("equals", String.class), new Object[]{})).isTrue();
  }

  @Test
  void passesCallToNonObjectLevelMethodWithCollidingNameAndDifferentNumberOfParams() throws Exception {
    when(configurationProvider.getProperty(eq("equals"), any(GenericTypeInterface.class))).thenReturn(true);
    BindInvocationHandler handler = new BindInvocationHandler(configurationProvider, "");

    assertThat((boolean) handler.invoke(this, this.getClass().getMethod("equals", String.class, String.class), new Object[]{})).isTrue();
  }

  @Test
  void invokesObjectLevelMethod() throws Exception {
    BindInvocationHandler handler = new BindInvocationHandler(configurationProvider, "");

    int hashCode = (int) handler.invoke(this, this.getClass().getMethod("hashCode"), new Object[]{});
    assertThat(hashCode).isEqualTo(handler.hashCode());
  }

  // For mocking java.lang.reflect.Method
  @SuppressWarnings("WeakerAccess")
  public String stringMethod() {
    return null;
  }

  @SuppressWarnings("WeakerAccess")
  public Map<List<Integer>, Boolean> mapMethod() {
    return null;
  }

  // Name collision with {@link Object#equals(Object)} (but with different parameters)
  @SuppressWarnings("WeakerAccess")
  public boolean equals(String param) {
    return true;
  }

  @SuppressWarnings("WeakerAccess")
  public boolean equals(String param1, String param2) {
    return true;
  }
}