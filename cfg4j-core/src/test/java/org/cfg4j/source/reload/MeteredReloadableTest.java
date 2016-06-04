package org.cfg4j.source.reload;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class MeteredReloadableTest {

  @Rule
  public ExpectedException expectedException = ExpectedException.none();

  @Mock
  private Reloadable delegate;

  @Mock
  private MetricRegistry metricRegistry;

  private MeteredReloadable reloadable;

  @Before
  public void setUp() throws Exception {
    Timer timer = mock(Timer.class);
    when(timer.time()).thenReturn(mock(Timer.Context.class));
    when(metricRegistry.timer(anyString())).thenReturn(timer);

    reloadable = new MeteredReloadable(metricRegistry, "configSource", delegate);
  }

  @Test
  public void reloadShouldCallDelegate() throws Exception {
    reloadable.reload();

    verify(delegate, times(1)).reload();
  }

  @Test
  public void reloadShouldPropagateIllegalStateExceptions() throws Exception {
    doThrow(new IllegalStateException("")).when(delegate).reload();

    expectedException.expect(IllegalStateException.class);
    reloadable.reload();
  }
}