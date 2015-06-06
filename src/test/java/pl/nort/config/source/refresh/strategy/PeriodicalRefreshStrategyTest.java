/*
 * Copyright 2015 Norbert Potocki (norbert.potocki@nort.pl)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package pl.nort.config.source.refresh.strategy;

import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import pl.nort.config.source.refresh.Refreshable;


@RunWith(MockitoJUnitRunner.class)
public class PeriodicalRefreshStrategyTest {

  @Rule
  public ExpectedException expectedException = ExpectedException.none();

  @Mock
  private Refreshable refreshable;

  @Test
  public void shouldRefreshImmediatelyAfterInit() throws Exception {
    PeriodicalRefreshStrategy strategy = new PeriodicalRefreshStrategy(60000);
    strategy.init(refreshable);
    strategy.shutdown();
    verify(refreshable, times(1)).refresh();
  }

  @Test
  public void shouldRefreshPeriodically() throws Exception {
    PeriodicalRefreshStrategy strategy = new PeriodicalRefreshStrategy(10);
    strategy.init(refreshable);
    Thread.sleep(50);
    strategy.shutdown();
    verify(refreshable, atLeast(3)).refresh();
  }
}