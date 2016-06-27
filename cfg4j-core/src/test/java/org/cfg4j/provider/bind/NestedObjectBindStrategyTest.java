package org.cfg4j.provider.bind;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class NestedObjectBindStrategyTest extends AbstractBindStrategyTest {
  NestedCustomTypeBindStrategy strategy;

  public interface SimpleConfig {
    Integer a();
  }

  public interface NestedConfig {
    SimpleConfig a();
  }

  @Before
  public void setUp() throws Exception {
    strategy = new NestedCustomTypeBindStrategy("org.cfg4j");
  }

  @Test
  public void dontApplyToBasicReturnType() throws Exception {
    assertThat(strategy.canApply(getMethod("a", SimpleConfig.class))).isFalse();
  }

  @Test
  public void dontApplyToPrimitiveReturnType() throws Exception {
    assertThat(strategy.canApply(getMethod("size", java.util.Map.class))).isFalse();
  }

  @Test
  public void dontApplyToForignNamespace() throws Exception {
    assertThat(strategy.canApply(getMethod("entrySet", java.util.Map.class))).isFalse();
  }


  @Test
  public void applyToNestedType() throws Exception {
    assertThat(strategy.canApply(getMethod("a", NestedConfig.class))).isTrue();
    strategy.apply(getMethod("a", NestedConfig.class), prefix, configurationProvider);
    verify(configurationProvider).bind("org.cfg4j.a", SimpleConfig.class);
  }}