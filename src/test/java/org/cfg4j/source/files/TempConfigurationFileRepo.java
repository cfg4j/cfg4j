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
package org.cfg4j.source.files;

import org.cfg4j.utils.FileUtils;
import org.eclipse.jgit.api.errors.GitAPIException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

/**
 * Temporary local git repository that contains configuration files.
 */
public class TempConfigurationFileRepo {

  protected Path dirPath;

  /**
   * Create temporary, local file repository. When you're done using it remove it by invoking {@link #remove()}
   * method.
   *
   * @throws IOException when unable to create local directories
   */
  public TempConfigurationFileRepo(String dirName) throws IOException, GitAPIException {
    dirPath = Files.createTempDirectory(dirName);
  }

  /**
   * Get absolute path to this repository.
   *
   * @return path to the repository
   */
  public String getURI() {
    return dirPath.toAbsolutePath().toString();
  }

  /**
   * Change the {@code key} property to {@code value} and store it in a {@code propFilePath} properties file. Commits
   * the change.
   *
   * @param propFilePath relative path to the properties file in this repository
   * @param key          property key
   * @param value        property value
   * @throws IOException     when unable to modify properties file
   * @throws GitAPIException when unable to commit changes
   */
  public void changeProperty(String propFilePath, String key, String value) throws IOException {
    createRequiredDirsForFile(propFilePath);
    writePropertyToFile(propFilePath, key, value);
  }

  private void createRequiredDirsForFile(String propFilePath) {
    int lastSlashPos = propFilePath.lastIndexOf("/") == -1 ? 0 : propFilePath.lastIndexOf("/");
    new File(getURI() + "/" + propFilePath.substring(0, lastSlashPos)).mkdirs();
  }

  /**
   * Delete file from this repository. Commits changes.
   *
   * @param filePath relative file path to delete
   * @throws GitAPIException when unable to commit changes
   */
  public void deleteFile(String filePath) throws IOException {
    if (!new File(dirPath + "/" + filePath).delete()) {
      throw new IllegalStateException("Unable to delete file: " + filePath);
    }
  }

  /**
   * Remove this repository.
   *
   * @throws IOException when unable to remove directory
   */
  public void remove() throws IOException {
    FileUtils.deleteDir(new File(getURI()));
  }

  private void writePropertyToFile(String propFilePath, String key, String value) throws IOException {
    OutputStream out = new FileOutputStream(getURI() + "/" + propFilePath);
    Properties properties = new Properties();
    properties.put(key, value);
    properties.store(out, "");
    out.close();
  }
}
