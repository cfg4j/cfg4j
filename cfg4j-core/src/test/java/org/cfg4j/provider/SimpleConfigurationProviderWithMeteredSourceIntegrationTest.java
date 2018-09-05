/*
 * Copyright 2015-2018 Norbert Potocki (norbert.potocki@nort.pl)
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

package org.cfg4j.provider;

import static org.assertj.core.api.Assertions.assertThat;

import com.codahale.metrics.MetricRegistry;
import org.cfg4j.source.ConfigurationSource;
import org.cfg4j.source.inmemory.InMemoryConfigurationSource;
import org.junit.jupiter.api.Test;

import java.util.Properties;


class SimpleConfigurationProviderWithMeteredSourceIntegrationTest {




  private MetricRegistry metricRegistry = new MetricRegistry();

  @Test
  void emitsMetrics() {
    ConfigurationProvider provider = getConfigurationProvider();

    provider.getProperty("some.setting", Boolean.class);

    assertThat(metricRegistry.getTimers()).containsOnlyKeys(
        "testService.allConfigurationAsProperties",
        "testService.getProperty",
        "testService.getPropertyGeneric",
        "testService.bind",
        "testService.source.getConfiguration",
        "testService.source.init",
        "testService.reloadable.reload"
    );
  }

  private ConfigurationProvider getConfigurationProvider() {
    Properties properties = new Properties();
    properties.put("some.setting", "true");

    ConfigurationSource source = new InMemoryConfigurationSource(properties);

    return new ConfigurationProviderBuilder()
        .withConfigurationSource(source)
        .withMetrics(metricRegistry, "testService.")
        .build();
  }
}