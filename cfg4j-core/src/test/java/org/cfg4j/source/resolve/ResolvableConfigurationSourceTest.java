package org.cfg4j.source.resolve;

import org.cfg4j.source.ConfigurationSource;
import org.cfg4j.source.context.environment.Environment;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import static org.assertj.core.api.Assertions.assertThat;
import static org.cfg4j.utils.PropertiesUtils.asMap;
import static org.mockito.Matchers.anyMap;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ResolvableConfigurationSourceTest {

  @Mock
  private ConfigurationSource underlyingSource;

  @Mock
  private PropertiesResolver propertiesResolver1;
  @Mock
  private PropertiesResolver propertiesResolver2;
  @Mock
  private Environment environment;

  private ResolvableConfigurationSource resolvableConfigurationSource;

  @Before
  public void init() {
    resolvableConfigurationSource = new ResolvableConfigurationSource(underlyingSource, Arrays.asList(propertiesResolver1, propertiesResolver2));
  }

  @Test
  public void testGetConfiguration() throws Exception {
    Properties sourceProperties = new Properties();
    sourceProperties.setProperty("k0", "0");
    when(underlyingSource.getConfiguration(environment)).thenReturn(sourceProperties);
    //Test replacement of key
    when(propertiesResolver1.resolve(eq(asMap("k0")))).thenReturn(asMap("k1", "0"));
    //Test addition of keys and values
    when(propertiesResolver2.resolve(eq(asMap("k1")))).thenReturn(asMap("k1", "1", "k2", "2", "k3", "k3"));
    assertThat(resolvableConfigurationSource.getConfiguration(environment)).isEqualTo(asMap("k1", "1", "k2", "2", "k3", "k3"));
  }

  @Test
  public void testInit() throws Exception {
    resolvableConfigurationSource.init();
    verify(underlyingSource).init();
  }



}