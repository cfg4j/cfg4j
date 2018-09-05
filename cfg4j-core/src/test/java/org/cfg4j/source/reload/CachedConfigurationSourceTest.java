package org.cfg4j.source.reload;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.entry;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import org.cfg4j.source.ConfigurationSource;
import org.cfg4j.source.context.environment.DefaultEnvironment;
import org.cfg4j.source.context.environment.Environment;
import org.cfg4j.source.context.environment.MissingEnvironmentException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Properties;


@ExtendWith(MockitoExtension.class)
class CachedConfigurationSourceTest {

  @Mock
  private ConfigurationSource delegateSource;
  private CachedConfigurationSource cachedConfigurationSource;

  @BeforeEach
  void setUp() {
    cachedConfigurationSource = new CachedConfigurationSource(delegateSource);
  }

  @Test
  void initPropagatesMissingEnvExceptions() {
    doThrow(new MissingEnvironmentException("")).when(delegateSource).init();

    assertThatThrownBy(() -> cachedConfigurationSource.init()).isExactlyInstanceOf(MissingEnvironmentException.class);
  }

  @Test
  void initPropagatesIllegalStateExceptions() {
    doThrow(new IllegalStateException("")).when(delegateSource).init();

    assertThatThrownBy(() -> cachedConfigurationSource.init()).isExactlyInstanceOf(IllegalStateException.class);

  }

  @Test
  void getConfigurationThrowsOnMissingEnvironment() {
    assertThatThrownBy(() -> cachedConfigurationSource.getConfiguration(new DefaultEnvironment())).isExactlyInstanceOf(MissingEnvironmentException.class);
  }

  @Test
  void getConfigurationReturnsReloadResult() {
    Properties properties = new Properties();
    when(delegateSource.getConfiguration(any(Environment.class))).thenReturn(properties);
    cachedConfigurationSource.reload(new DefaultEnvironment());

    assertThat(cachedConfigurationSource.getConfiguration(new DefaultEnvironment())).isEqualTo(properties);
  }

  @Test
  @Disabled("FIXME: implementation should be caching results")
  void getConfigurationDoesNotChangeValueBetweenReloads() {
    Properties properties = new Properties();
    properties.put("testConfig", "testValue");
    when(delegateSource.getConfiguration(any(Environment.class))).thenReturn(properties);

    cachedConfigurationSource.reload(new DefaultEnvironment());

    properties.put("testConfig", "testValueChanged");

    assertThat(cachedConfigurationSource.getConfiguration(new DefaultEnvironment())).contains(entry("testConfig", "testValue"));
  }

  @Test
  void reloadPropagatesMissingEnvExceptions() {
    when(delegateSource.getConfiguration(any(Environment.class))).thenThrow(new MissingEnvironmentException(""));

    assertThatThrownBy(() -> cachedConfigurationSource.reload(new DefaultEnvironment())).isExactlyInstanceOf(MissingEnvironmentException.class);
  }

  @Test
  void reloadPropagatesIllegalStateExceptions() {
    when(delegateSource.getConfiguration(any(Environment.class))).thenThrow(new IllegalStateException(""));

    assertThatThrownBy(() -> cachedConfigurationSource.reload(new DefaultEnvironment())).isExactlyInstanceOf(IllegalStateException.class);
  }
}