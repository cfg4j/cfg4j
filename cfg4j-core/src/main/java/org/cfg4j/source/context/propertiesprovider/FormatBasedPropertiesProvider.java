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

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

abstract class FormatBasedPropertiesProvider implements PropertiesProvider {

  /**
   * Flatten multi-level map.
   */
  @SuppressWarnings("unchecked")
  Map<String, Object> flatten(Map<String, Object> source) {
    Map<String, Object> result = new LinkedHashMap<>();

    for (String key : source.keySet()) {
      Object value = source.get(key);

      if (value instanceof Map) {
        Map<String, Object> subMap = flatten((Map<String, Object>) value);

        for (String subkey : subMap.keySet()) {
          result.put(key + "." + subkey, subMap.get(subkey));
        }
      } else if (value instanceof Collection) {
        StringBuilder joiner = new StringBuilder();
        String separator = "";

        for (Object element : ((Collection) value)) {
          Map<String, Object> subMap = flatten(Collections.singletonMap(key, element));
          joiner
              .append(separator)
              .append(subMap.entrySet().iterator().next().getValue().toString());

          separator = ",";
        }

        result.put(key, joiner.toString());
      } else {
        result.put(key, value);
      }
    }

    return result;
  }
}
