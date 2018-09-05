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

import static org.mockito.ArgumentMatchers.any;

import org.cfg4j.source.ConfigurationSource;
import org.cfg4j.source.context.environment.Environment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Properties;

@ExtendWith(MockitoExtension.class)
abstract class SimpleConfigurationProviderAbstractTest {

  SimpleConfigurationProvider simpleConfigurationProvider;

  @Mock
  protected ConfigurationSource configurationSource;

  @Mock
  protected Environment environment;

  @BeforeEach
  public void setUp() {
    simpleConfigurationProvider = new SimpleConfigurationProvider(configurationSource, environment);
  }

  Properties propertiesWith(String... args) {
    Properties properties = new Properties();
    for (int i = 1; i < args.length; i += 2) {
      properties.put(args[i - 1], args[i]);
    }

    return properties;
  }

  Environment anyEnvironment() {
    return any(Environment.class);
  }
}