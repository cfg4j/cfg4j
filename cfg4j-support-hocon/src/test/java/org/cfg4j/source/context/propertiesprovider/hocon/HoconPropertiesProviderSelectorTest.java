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

package org.cfg4j.source.context.propertiesprovider.hocon;

import org.cfg4j.source.context.propertiesprovider.DefaultPropertiesProviderSelector;
import org.cfg4j.source.context.propertiesprovider.PropertiesProvider;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.InputStream;
import java.util.Properties;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;


@RunWith(MockitoJUnitRunner.class)
public class HoconPropertiesProviderSelectorTest {

  @Rule
  public ExpectedException expectedException = ExpectedException.none();

  @Mock
  private PropertiesProvider propertiesProvider;

  @Mock
  private PropertiesProvider yamlProvider;

  @Mock
  private PropertiesProvider jsonProvider;

  @Mock
  private PropertiesProvider hoconProvider;
  private Properties hoconProperties;


  private DefaultPropertiesProviderSelector selector;

  @Before
  public void setUp() throws Exception {
    hoconProperties = new Properties();
    when(hoconProvider.getProperties(any(InputStream.class))).thenReturn(hoconProperties);

    selector = new HoconPropertiesProviderSelector(
      propertiesProvider,
      yamlProvider,
      jsonProvider,
      hoconProvider);
  }

  @Test
  public void returnsHOCONProviderForConf() throws Exception {
    assertThat(selector.getProvider("test.conf")).isEqualTo(hoconProvider);
  }

}