package org.cfg4j.source.annotation;

import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@Repeatable(DefaultValues.class)
public @interface DefaultValue {

	String value();

	String key() default "";

}
