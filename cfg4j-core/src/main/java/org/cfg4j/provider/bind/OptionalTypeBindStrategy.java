package org.cfg4j.provider.bind;

import org.cfg4j.provider.ConfigurationProvider;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.NoSuchElementException;
import java.util.Optional;

import static java.util.Objects.requireNonNull;

public class OptionalTypeBindStrategy extends PrefixBasedBindStrategy{

  public OptionalTypeBindStrategy() {
  }

  public boolean canApply(Method method) {
    return "java.util.Optional".equals(method.getReturnType().getName());
  }

  public Object apply(Method method, String prefix, ConfigurationProvider configurationProvider) {
    Type[] typeArguments = ((ParameterizedType) method.getGenericReturnType()).getActualTypeArguments();
    Class<?> typeClass = (Class<?>) typeArguments[0];
    try {
      return Optional.of(configurationProvider.bind(prefix + "." + method.getName(), typeClass));
    } catch (NoSuchElementException e) {
        return Optional.empty();
      }

  }

}
