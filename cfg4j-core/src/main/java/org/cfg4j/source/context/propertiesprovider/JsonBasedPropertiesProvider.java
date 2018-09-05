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

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

/**
 * {@link PropertiesProvider} that interprets given stream as JSON file.
 */
public class JsonBasedPropertiesProvider extends FormatBasedPropertiesProvider {

  /**
   * Get {@link Properties} for a given {@code inputStream} treating it as a JSON file.
   *
   * @param inputStream input stream representing JSON file
   * @return properties representing values from {@code inputStream}
   * @throws IllegalStateException when unable to read properties
   */
  @Override
  public Properties getProperties(InputStream inputStream) {
    requireNonNull(inputStream);

    Properties properties = new Properties();

    try {

      JSONTokener tokener = new JSONTokener(inputStream);
      if (tokener.end()) {
        return properties;
      }
      if (tokener.nextClean() == '"') {
        tokener.back();
        properties.put("content", tokener.nextValue().toString());
      } else {
        tokener.back();
        JSONObject obj = new JSONObject(tokener);

        Map<String, Object> yamlAsMap = convertToMap(obj);
        properties.putAll(flatten(yamlAsMap));
      }

      return properties;

    } catch (Exception e) {
      throw new IllegalStateException("Unable to load json configuration from provided stream", e);
    }
  }

  /**
   * Convert given Json document to a multi-level map.
   */
  @SuppressWarnings("unchecked")
  private Map<String, Object> convertToMap(Object jsonDocument) {
    Map<String, Object> jsonMap = new LinkedHashMap<>();

    // Document is a text block
    if (!(jsonDocument instanceof JSONObject)) {
      jsonMap.put("content", jsonDocument);
      return jsonMap;
    }

    JSONObject obj = (JSONObject) jsonDocument;
    for (String key : obj.keySet()) {
      Object value = obj.get(key);

      if (value instanceof JSONObject) {
        value = convertToMap(value);
      } else if (value instanceof JSONArray) {
        ArrayList<Map<String, Object>> collection = new ArrayList<>();

        for (Object element : ((JSONArray) value)) {
          collection.add(convertToMap(element));
        }

        value = collection;
      }

      jsonMap.put(key, value);
    }
    return jsonMap;

  }
}
