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

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import pl.nort.config.source.refresh.Refreshable;

@RunWith(MockitoJUnitRunner.class)
public class OnInitRefreshStrategyTest {

  @Mock
  private Refreshable resource;

  @Test
  public void shouldRefreshResourceOnce() throws Exception {
    OnInitRefreshStrategy refreshStrategy = new OnInitRefreshStrategy();
    refreshStrategy.init(resource);
    refreshStrategy.shutdown();

    verify(resource, times(1)).refresh();
  }

  @Test
  public void shouldNotRefreshAfterClose() throws Exception {
    OnInitRefreshStrategy refreshStrategy = new OnInitRefreshStrategy();
    refreshStrategy.init(resource);
    refreshStrategy.shutdown();

    verify(resource, times(1)).refresh();
  }
}