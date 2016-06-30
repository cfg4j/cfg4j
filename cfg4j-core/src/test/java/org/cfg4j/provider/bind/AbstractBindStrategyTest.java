package org.cfg4j.provider.bind;

import org.cfg4j.provider.ConfigurationProvider;
import org.mockito.Mock;

import java.lang.reflect.Method;

public class AbstractBindStrategyTest {
  protected String prefix = "org.cfg4j";
  @Mock
  protected ConfigurationProvider configurationProvider;

  protected Method getMethod(String methodName, Class<?> clazz) {
    try {
      return clazz.getMethod(methodName);
    } catch (NoSuchMethodException e) {
      throw new IllegalArgumentException("There is no method '" + methodName + "' in class " + clazz.getName());
    }
  }


}
