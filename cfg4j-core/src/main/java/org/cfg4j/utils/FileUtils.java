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
package org.cfg4j.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

/**
 * Operations on files.
 */
public class FileUtils {

  private static final Logger LOG = LoggerFactory.getLogger(FileUtils.class);

  /**
   * Delete directory or file.
   *
   * @param directory directory to delete
   * @throws IOException when directory can't be deleted
   */
  public void deleteDir(File directory) throws IOException {
    if (directory.exists()) {
      File[] files = directory.listFiles();

      if (files != null) {
        for (File file : files) {
          if (file.isDirectory()) {
            deleteDir(file);
          } else {
            LOG.debug("Removing file: " + directory.getAbsolutePath());
            if (!file.delete()) {
              throw new IOException("Unable to delete file: " + file.getAbsolutePath());
            }
          }
        }
      }

      LOG.debug("Removing directory: " + directory.getAbsolutePath());
      if (!directory.delete()) {
        throw new IOException("Unable to delete directory: " + directory.getAbsolutePath());
      }
    }
  }

}
