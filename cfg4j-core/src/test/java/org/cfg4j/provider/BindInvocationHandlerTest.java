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
    assertThat(captor.getValue().getType().getTypeName()).isEqualTo("java.util.Map<java.util.List<java.lang.Integer>, java.lang.Boolean>");
  }

  @Test
  public void shouldPassNoSuchElementException() throws Exception {
    when(configurationProvider.getProperty(anyString(), any(GenericTypeInterface.class))).thenThrow(new NoSuchElementException());
    BindInvocationHandler handler = new BindInvocationHandler(configurationProvider, "");

    expectedException.expect(NoSuchElementException.class);
    handler.invoke(this, this.getClass().getMethod("stringMethod"), new Object[]{});
  }

  @Test
  public void shouldPassIllegalArgumentException() throws Exception {
    when(configurationProvider.getProperty(anyString(), any(GenericTypeInterface.class))).thenThrow(new IllegalArgumentException());
    BindInvocationHandler handler = new BindInvocationHandler(configurationProvider, "");

    expectedException.expect(IllegalArgumentException.class);
    handler.invoke(this, this.getClass().getMethod("stringMethod"), new Object[]{});
  }

  @Test
  public void shouldPassIllegalStateException() throws Exception {
    when(configurationProvider.getProperty(anyString(), any(GenericTypeInterface.class))).thenThrow(new IllegalStateException());
    BindInvocationHandler handler = new BindInvocationHandler(configurationProvider, "");

    expectedException.expect(IllegalStateException.class);
    handler.invoke(this, this.getClass().getMethod("stringMethod"), new Object[]{});
  }


  @Test
  public void shouldNotPassCallToGetClass() throws Exception {
    when(configurationProvider.getProperty(eq("getClass"), any(GenericTypeInterface.class))).thenThrow(new NoSuchElementException());
    BindInvocationHandler handler = new BindInvocationHandler(configurationProvider, "");

    handler.invoke(this, this.getClass().getMethod("getClass"), new Object[]{});
  }

  @Test
  public void shouldNotPassCallToHashCode() throws Exception {
    when(configurationProvider.getProperty(eq("hashCode"), any(GenericTypeInterface.class))).thenThrow(new NoSuchElementException());
    BindInvocationHandler handler = new BindInvocationHandler(configurationProvider, "");

    handler.invoke(this, this.getClass().getMethod("hashCode"), new Object[]{});
  }

  @Test
  public void shouldNotPassCallToEquals() throws Exception {
    when(configurationProvider.getProperty(eq("equals"), any(GenericTypeInterface.class))).thenThrow(new NoSuchElementException());
    BindInvocationHandler handler = new BindInvocationHandler(configurationProvider, "");

    handler.invoke(this, this.getClass().getMethod("equals", Object.class), new Object[]{});
  }

  @Test
  public void shouldNotPassCallToToString() throws Exception {
    when(configurationProvider.getProperty(eq("toString"), any(GenericTypeInterface.class))).thenThrow(new NoSuchElementException());
    BindInvocationHandler handler = new BindInvocationHandler(configurationProvider, "");

    handler.invoke(this, this.getClass().getMethod("toString"), new Object[]{});
  }

  @Test
  public void shouldNotPassCallToNotify() throws Exception {
    when(configurationProvider.getProperty(eq("notify"), any(GenericTypeInterface.class))).thenThrow(new NoSuchElementException());
    BindInvocationHandler handler = new BindInvocationHandler(configurationProvider, "");

    handler.invoke(this, this.getClass().getMethod("notify"), new Object[]{});
  }

  @Test
  public void shouldNotPassCallToNotifyAll() throws Exception {
    when(configurationProvider.getProperty(eq("notifyAll"), any(GenericTypeInterface.class))).thenThrow(new NoSuchElementException());
    BindInvocationHandler handler = new BindInvocationHandler(configurationProvider, "");

    handler.invoke(this, this.getClass().getMethod("notifyAll"), new Object[]{});
  }

  @Test
  public void shouldNotPassCallToWait() throws Exception {
    when(configurationProvider.getProperty(eq("wait"), any(GenericTypeInterface.class))).thenThrow(new NoSuchElementException());
    BindInvocationHandler handler = new BindInvocationHandler(configurationProvider, "");

    handler.invoke(this, this.getClass().getMethod("wait"), new Object[]{});
  }

  @Test
  public void shouldNotPassCallToWait2() throws Exception {
    when(configurationProvider.getProperty(eq("wait"), any(GenericTypeInterface.class))).thenThrow(new NoSuchElementException());
    BindInvocationHandler handler = new BindInvocationHandler(configurationProvider, "");

    handler.invoke(this, this.getClass().getMethod("wait", long.class), new Object[]{});
  }

  @Test
  public void shouldNotPassCallToWait3() throws Exception {
    when(configurationProvider.getProperty(eq("wait"), any(GenericTypeInterface.class))).thenThrow(new NoSuchElementException());
    BindInvocationHandler handler = new BindInvocationHandler(configurationProvider, "");

    handler.invoke(this, this.getClass().getMethod("wait", long.class, int.class), new Object[]{});
  }

  @Test
  public void shouldPassCallToNonObjectMethodWithCollidingName() throws Exception {
    when(configurationProvider.getProperty(eq("equals"), any(GenericTypeInterface.class))).thenReturn(true);
    BindInvocationHandler handler = new BindInvocationHandler(configurationProvider, "");

    assertThat((boolean) handler.invoke(this, this.getClass().getMethod("equals", String.class), new Object[]{})).isTrue();
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
}