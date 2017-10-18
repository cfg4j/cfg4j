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

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigRenderOptions;
import org.cfg4j.source.context.propertiesprovider.JsonBasedPropertiesProvider;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

public class HoconBasedPropertiesProvider extends JsonBasedPropertiesProvider {
  @Override
  public Properties getProperties(InputStream inputStream) {

    InputStreamReader reader = new InputStreamReader(inputStream);

    Config config = ConfigFactory.parseReader(reader);

    //Render to JSON (no whitespace or comments)
    String conciseRendered = config.root().render(ConfigRenderOptions.concise());

    byte[] bytes = conciseRendered.getBytes(StandardCharsets.UTF_8);

    InputStream modified = new ByteArrayInputStream(bytes);

    return super.getProperties(modified);
  }
}
