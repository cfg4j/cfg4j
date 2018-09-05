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

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Represents a generic type {@code T}
 *
 * @param <T> generic type
 */
public abstract class GenericType<T> implements GenericTypeInterface {

  private final Type type;

  public GenericType() {

    if (GenericType.class != getClass().getSuperclass()) {
      throw new IllegalArgumentException("You must extend GenericType class directly");
    }

    Type t = getClass().getGenericSuperclass();

    if (t instanceof ParameterizedType) {
      ParameterizedType superClass = (ParameterizedType) t;
      type = superClass.getActualTypeArguments()[0];
    } else {
      throw new IllegalArgumentException("You must use parametrized type");
    }
  }

  @Override
  public Type getType() {
    return type;
  }

  @Override
  public String toString() {
    return "GenericType{" +
        "type=" + type +
        '}';
  }
}
