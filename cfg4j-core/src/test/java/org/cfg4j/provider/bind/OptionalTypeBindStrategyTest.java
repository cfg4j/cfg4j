package org.cfg4j.provider.bind;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.lang.reflect.Method;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.cfg4j.utils.PropertiesUtils.propertiesWith;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class OptionalTypeBindStrategyTest extends AbstractBindStrategyTest {

  public interface OptionalConfig {
    Optional<Integer> a();
  }

  public interface SimpleConfig {
    Integer a();
  }

  private OptionalTypeBindStrategy optionalTypeBindStrategy;

  @Before
  public void init() {
    optionalTypeBindStrategy = new OptionalTypeBindStrategy();
  }

  @Test
  public void testApplyToOptional() {
    Method methodA = getMethod("a", OptionalConfig.class);
    assertTrue(optionalTypeBindStrategy.canApply(methodA));
  }

  @Test
  public void testApplyToNonOptional() {
    Method methodA = getMethod("a", SimpleConfig.class);
    assertFalse(optionalTypeBindStrategy.canApply(methodA));
  }


  @Test
  public void bindToOptionalOfIntWithValue() {
    when(configurationProvider.bind("org.cfg4j.a", Integer.class)).thenReturn(1);


    Optional<Integer> result = testMethodAWithProps(propertiesWith(
      "org.cfg4j.a", "1"));

    assertThat(result.get()).isEqualTo(1);
  }

  @Test
  public void bindToOptionalOfIntWithoutValue() {
    when(configurationProvider.bind("org.cfg4j.a", Integer.class)).thenThrow(NoSuchElementException.class);

    Optional<Integer> result = testMethodAWithProps(propertiesWith(
      "org.cfg4j.a", "1"));

    assertFalse(result.isPresent());
  }



  @SuppressWarnings("unchecked")
  private <T> Optional<T> testMethodAWithProps(Properties properties) {
    when(configurationProvider.allConfigurationAsProperties()).thenReturn(properties);
    Method methodA = getMethod("a", OptionalConfig.class);
    assertTrue(optionalTypeBindStrategy.canApply(methodA));
    return (Optional<T>) optionalTypeBindStrategy.apply(methodA, prefix, configurationProvider);
  }

}