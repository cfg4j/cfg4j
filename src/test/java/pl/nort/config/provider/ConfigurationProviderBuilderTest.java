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

package pl.nort.config.provider;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import pl.nort.config.source.ConfigurationSource;
import pl.nort.config.source.refresh.RefreshStrategy;
import pl.nort.config.source.refresh.Refreshable;

@RunWith(MockitoJUnitRunner.class)
public class ConfigurationProviderBuilderTest {

  private ConfigurationProviderBuilder builder;

  @Before
  public void setUp() throws Exception {
    builder = new ConfigurationProviderBuilder();
  }

  @Test
  public void initializesStrategyOnBuild() throws Exception {
    RefreshStrategy refreshStrategy = mock(RefreshStrategy.class);
    builder
        .withRefreshStrategy(refreshStrategy)
        .build();

    verify(refreshStrategy, times(1)).init(any(Refreshable.class));
  }

  @Test
  public void passesSourceToRefreshStrategy() throws Exception {
    ConfigurationSource configurationSource = mock(ConfigurationSource.class);
    RefreshStrategy refreshStrategy = mock(RefreshStrategy.class);
    builder
        .withConfigurationSource(configurationSource)
        .withRefreshStrategy(refreshStrategy)
        .build();

    verify(refreshStrategy, times(1)).init(configurationSource);
  }
}