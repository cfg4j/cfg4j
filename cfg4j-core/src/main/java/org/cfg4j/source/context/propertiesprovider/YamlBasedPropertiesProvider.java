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

import static java.util.Objects.requireNonNull;

import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.reader.UnicodeReader;
import org.yaml.snakeyaml.scanner.ScannerException;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

/**
 * {@link PropertiesProvider} that interprets given stream as YAML file.
 */
public class YamlBasedPropertiesProvider extends FormatBasedPropertiesProvider {

  /**
   * Get {@link Properties} for a given {@code inputStream} treating it as a YAML file.
   *
   * @param inputStream input stream representing YAML file
   * @return properties representing values from {@code inputStream}
   * @throws IllegalStateException when unable to read properties
   */
  @Override
  public Properties getProperties(InputStream inputStream) {
    requireNonNull(inputStream);

    Yaml yaml = new Yaml();

    Properties properties = new Properties();

    try (Reader reader = new UnicodeReader(inputStream)) {

      Object object = yaml.load(reader);

      if (object != null) {
        Map<String, Object> yamlAsMap = convertToMap(object);
        properties.putAll(flatten(yamlAsMap));
      }

      return properties;

    } catch (IOException | ScannerException e) {
      throw new IllegalStateException("Unable to load yaml configuration from provided stream", e);
    }
  }

  /**
   * Convert given Yaml document to a multi-level map.
   */
  @SuppressWarnings("unchecked")
  private Map<String, Object> convertToMap(Object yamlDocument) {

    Map<String, Object> yamlMap = new LinkedHashMap<>();

    // Document is a text block
    if (!(yamlDocument instanceof Map)) {
      yamlMap.put("content", yamlDocument);
      return yamlMap;
    }

    for (Map.Entry<Object, Object> entry : ((Map<Object, Object>) yamlDocument).entrySet()) {
      Object value = entry.getValue();

      if (value instanceof Map) {
        value = convertToMap(value);
      } else if (value instanceof Collection) {
        ArrayList<Map<String, Object>> collection = new ArrayList<>();

        for (Object element : ((Collection) value)) {
          collection.add(convertToMap(element));
        }

        value = collection;
      }

      yamlMap.put(entry.getKey().toString(), value);
    }
    return yamlMap;
  }
}