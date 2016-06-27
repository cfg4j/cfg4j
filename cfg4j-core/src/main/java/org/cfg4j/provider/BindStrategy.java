package org.cfg4j.provider;

import java.io.File;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;
import java.net.URL;
import java.util.Collection;
import java.util.NoSuchElementException;

/**
 * Bind strategy allows to implement custom binding of types during call of {@link ConfigurationProvider#bind(String, Class)}.
 */
public interface BindStrategy {

  /**
   * Determines acceptance of current strategy to provided method.
   *
   * @param method Method of class or interface received through reflection API
   * @return true if method can be processed by current bind strategy
   */
  boolean canApply(Method method);


  /**
   * Applies bind strategy to provided method
   * {@link ConfigurationProvider#getProperty(String, Class)} method.
   *
   * @param method Method of class or interface received through reflection API
   * @param prefix                prefix to construct path/subpath of property to finally call {@link ConfigurationProvider#getProperty(String, Class)}
   * @param configurationProvider configuration provider to use for fetching properties
   * @return result object that can be concrete value or proxy
   */
  Object apply(Method method, String prefix, ConfigurationProvider configurationProvider);
}
