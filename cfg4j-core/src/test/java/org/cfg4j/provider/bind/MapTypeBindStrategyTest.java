package org.cfg4j.provider.bind;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import static org.assertj.core.api.Assertions.assertThat;
import static org.cfg4j.provider.ConfigurationProviderAbstractTest.propertiesWith;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class MapTypeBindStrategyTest extends AbstractBindStrategyTest {

  public interface MapConfig {
    Map<String, Integer> a();
  }

  public interface InvalidMapConfig {
    Map<Integer, Integer> a();
  }

  public interface SimpleConfig {
    Integer a();
  }

  private MapTypeBindStrategy mapBindStrategy;

  @Before
  public void init() {
    mapBindStrategy = new MapTypeBindStrategy();
  }

  @Test(expected = IllegalStateException.class)
  public void testApplyToMapOfNonStringKeys() {
    Method methodA = getMethod("a", InvalidMapConfig.class);
    assertTrue(mapBindStrategy.canApply(methodA));
    mapBindStrategy.apply(methodA, prefix, configurationProvider);
  }

  @Test
  public void testApplyToNonMap() {
    Method methodA = getMethod("a", SimpleConfig.class);
    assertThat(mapBindStrategy.canApply(methodA)).isFalse();
  }


  @Test
  public void bindToMapOfInts() {
    when(configurationProvider.bind("org.cfg4j.a.a", Integer.class)).thenReturn(1);
    when(configurationProvider.bind("org.cfg4j.a.b", Integer.class)).thenReturn(2);
    when(configurationProvider.bind("org.cfg4j.a.c", Integer.class)).thenReturn(3);

    HashMap<String, Integer> resultMap = testMethodAWithProps(propertiesWith(
      "org.cfg4j.a.a", "1",
      "org.cfg4j.a.b", "2",
      "org.cfg4j.a.c", "3"));

    assertThat(resultMap).containsEntry("a", 1)
      .containsEntry("b", 2)
      .containsEntry("c", 3);
  }

  @Test
  public void bindEmptyMap() {
    Properties properties = propertiesWith();
    assertThat(testMethodAWithProps(properties)).isEmpty();
  }

  @Test
  public void filterKeysBeforBindToMap() {
    assertThat(testMethodAWithProps(propertiesWith(
      "org.cfg4j.a1.a", "1",
      "org.cfg4j.b.a", "2",
      "org.cfg4j", "2"
    ))).isEmpty();
  }

  @SuppressWarnings("unchecked")
  private <T> HashMap<String, T> testMethodAWithProps(Properties properties) {
    when(configurationProvider.allConfigurationAsProperties()).thenReturn(properties);
    Method methodA = getMethod("a", MapConfig.class);
    assertTrue(mapBindStrategy.canApply(methodA));
    return (HashMap<String, T>) mapBindStrategy.apply(methodA, prefix, configurationProvider);
  }

}