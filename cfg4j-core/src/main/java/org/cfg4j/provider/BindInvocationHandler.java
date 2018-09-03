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

import static java.util.Objects.requireNonNull;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class BindInvocationHandler implements InvocationHandler {

  protected final ConfigurationProvider configurationProvider;
  protected final String prefix;
  protected final List<BindStrategy> bindStrategies;

  /**
   * Create invocation handler which fetches property from given {@code configurationProvider} using call to
   * {@link ConfigurationProvider#getProperty(String, Class)} method.
   *
   * @param configurationProvider configuration provider to use for fetching properties
   * @param prefix                prefix for calls to {@link ConfigurationProvider#getProperty(String, Class)}
   * @param bindStrategies        list of strategies that will by tried to apply during proy invocation}
   */
  BindInvocationHandler(ConfigurationProvider configurationProvider, String prefix, List<BindStrategy> bindStrategies) {
    this.configurationProvider = requireNonNull(configurationProvider);
    this.prefix = requireNonNull(prefix);
    this.bindStrategies = requireNonNull(bindStrategies);
  }


  /**
   * Create invocation handler which fetches property from given {@code configurationProvider} using call to
   * {@link ConfigurationProvider#getProperty(String, Class)} method.
   *
   * @param configurationProvider configuration provider to use for fetching properties
   * @param prefix                prefix for calls to {@link ConfigurationProvider#getProperty(String, Class)}
   */
  BindInvocationHandler(ConfigurationProvider configurationProvider, String prefix) {
    this(configurationProvider, prefix, new ArrayList<BindStrategy>());
  }

  /**
   * Sequentially calls chain of bind strategies till acceptable is found
   *
   * @throws InvocationTargetException when invoked an Object-level (e.g. {@link Object#hashCode()}) method and it throws an exception.
   * @throws IllegalAccessException    when invoked an Object-level (e.g. {@link Object#hashCode()}) method and it is inaccessible.
   * @throws IllegalStateException     when any applicable bind strategy is not found
   */
  @Override
  public Object invoke(Object proxy, Method method, Object[] args) throws InvocationTargetException, IllegalAccessException {
    if (isObjectMethod(method)) {
      return method.invoke(this, args);
    }

    for (BindStrategy bindStrategy : bindStrategies) {
      if (bindStrategy.canApply(method)) {
        return bindStrategy.apply(method, prefix, configurationProvider);
      }
    }
    final Type returnType = method.getGenericReturnType();
    return configurationProvider.getProperty(prefix + (prefix.isEmpty() ? "" : ".") + method.getName(), new GenericTypeInterface() {
      @Override
      public Type getType() {
        return returnType;
      }
    });
  }

  /**
   * Check if method is defined by Object class (e.g. {@link Object#hashCode()}.
   */
  private boolean isObjectMethod(Method method) {
    for (Method objectMethod : Object.class.getMethods()) {
      if (method.getName().equals(objectMethod.getName())) {
        if (equalParamTypes(objectMethod.getParameterTypes(), method.getParameterTypes())) {
          return true;
        }
      }
    }

    return false;
  }

  /**
   * Check if two arrays of parameter types are equal.
   */
  private boolean equalParamTypes(Class<?>[] params1, Class<?>[] params2) {
    if (params1.length == params2.length) {
      for (int i = 0; i < params1.length; i++) {
        if (params1[i] != params2[i]) {
          return false;
        }
      }
      return true;
    }
    return false;
  }

}
