package org.cfg4j.source.annotation;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Properties;

import org.cfg4j.source.context.environment.DefaultEnvironment;
import org.cfg4j.source.context.environment.Environment;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class AnnotationConfigurationSourceTest {

  private AnnotationConfigurationSource source;
  private Environment environment = new DefaultEnvironment();

  @Before
  public void setUp() throws Exception {
    source = new AnnotationConfigurationSource(AnnotatedTestConfig.class, "somePrefix");
    source.init();
  }

  @Test
  public void getConfigurationFromAnnotations() throws Exception {
    Properties properties = source.getConfiguration(environment);
    assertThat(properties.get("somePrefix.booleanValue")).isEqualTo("true");
    assertThat(properties.get("somePrefix.stringValue")).isEqualTo("someValue");
    assertThat(properties.get("somePrefix.intValue")).isEqualTo("13");
    
    // note that there is no null support by properties, a bit unfortunate
    assertThat(properties.get("somePrefix.someNullableValue")).isEqualTo(""); 
  }
}