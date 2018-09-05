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

package org.cfg4j.source.context.propertiesprovider;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * {@link PropertiesProvider} that interprets given stream as properties file.
 */
public class PropertyBasedPropertiesProvider implements PropertiesProvider {

  /**
   * Get {@link Properties} for a given {@code inputStream} treating it as a properties file.
   *
   * @param inputStream input stream representing properties file
   * @return properties representing values from {@code inputStream}
   * @throws IllegalStateException when unable to read properties
   */
  @Override
  public Properties getProperties(InputStream inputStream) {
    Properties properties = new Properties();

    try {
      properties.load(inputStream);
    } catch (IOException | IllegalArgumentException e) {
      throw new IllegalStateException("Unable to load properties from provided stream", e);
    }

    return properties;
  }
}
