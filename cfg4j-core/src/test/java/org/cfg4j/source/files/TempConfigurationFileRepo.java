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
package org.cfg4j.source.files;

import org.cfg4j.utils.FileUtils;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Temporary local file repository that contains configuration files.
 */
public class TempConfigurationFileRepo {

  public Path dirPath;

  /**
   * Create temporary, local file repository. When you're done using it remove it by invoking {@link #remove()}
   * method.
   *
   * @throws IOException when unable to create local directories
   */
  public TempConfigurationFileRepo(String dirName) throws IOException {
    dirPath = Files.createTempDirectory(dirName);
  }

  /**
   * Change the {@code key} property to {@code value} and store it in a {@code propFilePath} properties file
   *
   * @param propFilePath relative path to the properties file in this repository
   * @param key          property key
   * @param value        property value
   * @throws IOException     when unable to modify properties file
   */
  public void changeProperty(Path propFilePath, String key, String value) throws IOException {
    Files.createDirectories(dirPath.resolve(propFilePath).getParent());
    writePropertyToFile(propFilePath, key, value);
  }

  /**
   * Delete file from this repository.
   *
   * @param filePath relative file path to delete
   */
  public void deleteFile(Path filePath) throws IOException {
    new FileUtils().deleteDir(dirPath.resolve(filePath));
  }

  /**
   * Remove this repository.
   *
   * @throws IOException when unable to remove directory
   */
  public void remove() throws IOException {
    new FileUtils().deleteDir(dirPath);
  }

  private void writePropertyToFile(Path propFilePath, String key, String value) throws IOException {
    OutputStream out = new FileOutputStream(dirPath.resolve(propFilePath).toFile());
    out.write((key + "=" + value).getBytes());
    out.close();
  }
}
