/*
 * Copyright 2015-2016 Norbert Potocki (norbert.potocki@nort.pl)
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

import org.cfg4j.provider.bind.MapTypeBindStrategy;
import org.cfg4j.provider.bind.NestedCustomTypeBindStrategy;
import org.cfg4j.source.ConfigurationSource;
import org.cfg4j.source.context.environment.Environment;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Arrays;

import static org.mockito.Matchers.any;

@RunWith(MockitoJUnitRunner.class)
public abstract class ConfigurationProviderAbstractTest {

  @Rule
  public ExpectedException expectedException = ExpectedException.none();

  protected ConfigurationProvider configurationProvider;

  @Mock
  protected ConfigurationSource configurationSource;

  @Mock
  protected Environment environment;

  @Before
  public void setUp() throws Exception {
    configurationProvider = new SimpleConfigurationProvider(configurationSource, environment, Arrays.<BindStrategy>asList(new NestedCustomTypeBindStrategy("org.cfg4j"), new MapTypeBindStrategy()));
  }

  protected Environment anyEnvironment() {
    return any(Environment.class);
  }
}