package org.cfg4j.source.annotation;

public interface AnnotatedTestConfig {

  @DefaultValue("true")
  public boolean booleanValue();

  @DefaultValue("someValue")
  public String stringValue();

  @DefaultValue("13")
  public int intValue();

  @DefaultNull()
  public String someNullableValue();

}
