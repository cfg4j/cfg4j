package org.cfg4j.source.reload;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


@ExtendWith(MockitoExtension.class)
class MeteredReloadableTest {


  @Mock
  private Reloadable delegate;

  @Mock
  private MetricRegistry metricRegistry;

  private MeteredReloadable reloadable;

  @BeforeEach
  void setUp() {
    Timer timer = mock(Timer.class);
    when(timer.time()).thenReturn(mock(Timer.Context.class));
    when(metricRegistry.timer(anyString())).thenReturn(timer);

    reloadable = new MeteredReloadable(metricRegistry, "configSource", delegate);
  }

  @Test
  void reloadCallsDelegate() {
    reloadable.reload();

    verify(delegate, times(1)).reload();
  }

  @Test
  void reloadPropagatesIllegalStateExceptions() {
    doThrow(new IllegalStateException("")).when(delegate).reload();

    assertThatThrownBy(() -> reloadable.reload()).isExactlyInstanceOf(IllegalStateException.class);
  }
}