/*
 * Copyright 2015 Norbert Potocki (norbert.potocki@nort.pl)
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

import java.io.IOException;

/**
 * Classpath files repository.
 */
public class TempConfigurationClasspathRepo {

  /**
   * Create repository allowing access to classpath files.
   */
  public TempConfigurationClasspathRepo() {
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
    writePropertyToFile(propFilePath, key, value);
  }

  /**
   * Delete file from this repository.
   *
   * @param filePath relative file path to delete
   */
  public void deleteFile(String filePath) throws IOException {
//    if (!new File(dirPath + "/" + filePath).delete()) {
//      throw new IllegalStateException("Unable to delete file: " + filePath);
//    }
  }

  private void writePropertyToFile(String propFilePath, String key, String value) throws IOException {
//    OutputStream out = new FileOutputStream(getURI() + "/" + propFilePath);
//    Properties properties = new Properties();
//    properties.put(key, value);
//    properties.store(out, "");
//    out.close();
  }
}
