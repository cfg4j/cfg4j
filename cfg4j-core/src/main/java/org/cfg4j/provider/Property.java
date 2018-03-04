package org.cfg4j.provider;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation is used to override property access on a class being bound by a {@link ConfigurationProvider}.
 */
@Target({ ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface Property {

  /**
   * This is the override for property coordinate access.
   * @return The coordinate to read property values from.
   */
  String value();

  /**
   * This allows binding coordinates to be specified either as 'detached' from the current context (the default) or simply as a renaming or deep mapping.
   * @return Whether or not to apply the prefix present in the binding context.
   */
  boolean applyPrefix() default false;

}
