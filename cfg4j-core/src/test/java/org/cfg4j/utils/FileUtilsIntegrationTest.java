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

package org.cfg4j.utils;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;


class FileUtilsIntegrationTest {




  private FileUtils fileUtils;

  @BeforeEach
  void setUp() {
    fileUtils = new FileUtils();
  }

  @Test
  void deletesFile() throws Exception {
    Path tempFile = Files.createTempFile("", "");

    fileUtils.deleteDir(tempFile);

    assertThat(Files.exists(tempFile)).isFalse();
  }

  @Test
  void deletesDirectory() throws Exception {
    Path tempDirectory = Files.createTempDirectory("");

    fileUtils.deleteDir(tempDirectory);

    assertThat(Files.exists(tempDirectory)).isFalse();
  }


  @Test
  void recursivelyDeletesFiles() throws Exception {
    Path tempDirectory = Files.createTempDirectory("");
    Files.createTempFile(tempDirectory, "", "");

    fileUtils.deleteDir(tempDirectory);

    assertThat(Files.exists(tempDirectory)).isFalse();
  }

  @Test
  void recursivelyDeletesDirectories() throws Exception {
    Path tempDirectory = Files.createTempDirectory("");
    Files.createTempDirectory(tempDirectory, "");

    fileUtils.deleteDir(tempDirectory);

    assertThat(Files.exists(tempDirectory)).isFalse();
  }

}