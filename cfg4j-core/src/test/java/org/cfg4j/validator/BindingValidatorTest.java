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

package org.cfg4j.validator;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;

@RunWith(MockitoJUnitRunner.class)
public class BindingValidatorTest {

  public interface ConfigPojo {
    Integer someSetting();

    List<Boolean> otherSetting();
  }

  private BindingValidator bindingValidator;

  @Mock
  private ConfigPojo configPojo;

  @Rule
  public ExpectedException expectedException = ExpectedException.none();

  @Before
  public void setUp() throws Exception {
    bindingValidator = new BindingValidator();

    when(configPojo.someSetting()).thenReturn(0);
    when(configPojo.otherSetting()).thenReturn(Collections.<Boolean>emptyList());
  }

  @Test
  public void shouldInvokeAllMethods() throws Exception {
    bindingValidator.validate(configPojo, ConfigPojo.class);

    verify(configPojo, times(1)).someSetting();
    verify(configPojo, times(1)).otherSetting();
  }

  @Test
  public void shouldPropagateNoSuchElementExceptionsFromInvocation() throws Exception {
    when(configPojo.someSetting()).thenThrow(NoSuchElementException.class);

    expectedException.expect(NoSuchElementException.class);
    bindingValidator.validate(configPojo, ConfigPojo.class);
  }

  @Test
  public void shouldPropagateIllegalArgumentExceptionsFromInvocation() throws Exception {
    when(configPojo.someSetting()).thenThrow(IllegalArgumentException.class);

    expectedException.expect(IllegalArgumentException.class);
    bindingValidator.validate(configPojo, ConfigPojo.class);
  }

  @Test
  public void shouldThrowOnOtherExceptions() throws Exception {
    when(configPojo.someSetting()).thenThrow(IllegalAccessException.class);

    expectedException.expect(IllegalStateException.class);
    bindingValidator.validate(configPojo, ConfigPojo.class);
  }
}