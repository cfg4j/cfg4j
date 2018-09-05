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

package org.cfg4j.validator;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;


@ExtendWith(MockitoExtension.class)
class BindingValidatorTest {

  interface ConfigPojo {
    Integer someSetting();

    List<Boolean> otherSetting();
  }

  private BindingValidator bindingValidator;

  @Mock
  private ConfigPojo configPojo;


  @BeforeEach
  void setUp() {
    bindingValidator = new BindingValidator();

    when(configPojo.someSetting()).thenReturn(0);
  }

  @Test
  void invokesAllMethods() {
    when(configPojo.otherSetting()).thenReturn(Collections.<Boolean>emptyList());

    bindingValidator.validate(configPojo, ConfigPojo.class);

    verify(configPojo, times(1)).someSetting();
    verify(configPojo, times(1)).otherSetting();
  }

  @Test
  void propagatesNoSuchElementExceptionsFromInvocation() {
    when(configPojo.someSetting()).thenThrow(NoSuchElementException.class);

    assertThatThrownBy(() -> bindingValidator.validate(configPojo, ConfigPojo.class)).isExactlyInstanceOf(NoSuchElementException.class);
  }

  @Test
  void propagatesIllegalArgumentExceptionsFromInvocation() {
    when(configPojo.someSetting()).thenThrow(IllegalArgumentException.class);

    assertThatThrownBy(() -> bindingValidator.validate(configPojo, ConfigPojo.class)).isExactlyInstanceOf(IllegalArgumentException.class);
  }

  @Test
  void throwsOnOtherExceptions() {
    when(configPojo.someSetting()).thenThrow(IllegalStateException.class);

    assertThatThrownBy(() -> bindingValidator.validate(configPojo, ConfigPojo.class)).isExactlyInstanceOf(IllegalStateException.class);
  }
}