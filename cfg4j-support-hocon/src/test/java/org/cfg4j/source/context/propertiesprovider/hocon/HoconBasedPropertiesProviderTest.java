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

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.InputStream;
import java.util.Objects;
import java.util.Properties;


@RunWith(MockitoJUnitRunner.class)
public class HoconBasedPropertiesProviderTest {

  @Rule
  public ExpectedException expectedException = ExpectedException.none();

  private HoconBasedPropertiesProvider provider;

  @Before
  public void Before() {
    provider = new HoconBasedPropertiesProvider();
  }

  @Test
  public void hoconToProperties() throws Exception {

    String path = "test-hocon.conf";

    try (InputStream input = getClass().getClassLoader().getResourceAsStream(path)) {

      Properties properties = provider.getProperties(input);

      String actual = properties.get("zepto.queue.size").toString();
      String expected = "666";

      Assert.assertTrue(Objects.equals(actual, expected));
    }
  }
}