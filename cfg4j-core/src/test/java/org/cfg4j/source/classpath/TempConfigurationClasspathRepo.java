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
package org.cfg4j.source.classpath;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Classpath files repository.
 */
public class TempConfigurationClasspathRepo {

  private Map<String, String> originalContents;

  /**
   * Create repository allowing access to classpath files. After you're done working with this repository
   * make a call to the {@link #close()} method to restore original contents of the modified files.
   */
  public TempConfigurationClasspathRepo() {
    originalContents = new HashMap<>();
  }

  /**
   * Change the {@code key} property to {@code value} and store it in a {@code propFilePath} properties file.
   *
   * @param propFilePath relative path to the properties file in this repository
   * @param key          property key
   * @param value        property value
   * @throws IOException when unable to modify properties file
   */
  public void changeProperty(String propFilePath, String key, String value) throws IOException {
    writeToFile(propFilePath, key + "=" + value);
  }

  /**
   * Close this repository and restore all modified files to their original form.
   */
  public void close() throws IOException {
    for (String propFilePath : originalContents.keySet()) {
      writeToFile(propFilePath, originalContents.get(propFilePath));
    }
  }

  private void writeToFile(String propFilePath, String content) throws IOException {
    URL systemResource = ClassLoader.getSystemResource(propFilePath);

    // Store original contents
    if (!originalContents.containsKey(propFilePath)) {
      InputStream inputStream = new FileInputStream(systemResource.getPath());
      try (java.util.Scanner s = new java.util.Scanner(inputStream)) {
        originalContents.put(propFilePath, s.useDelimiter("\\A").hasNext() ? s.next() : "");
      }
    }

    OutputStream out = new FileOutputStream(systemResource.getPath());
    out.write((content).getBytes());
    out.close();
  }

}
