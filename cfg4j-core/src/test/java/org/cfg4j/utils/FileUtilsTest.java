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

import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.File;
import java.io.IOException;


@RunWith(MockitoJUnitRunner.class)
public class FileUtilsTest {

  @Rule
  public ExpectedException expectedException = ExpectedException.none();

  @Mock
  private File file;

  private FileUtils fileUtils;

  @Before
  public void setUp() throws Exception {
    when(file.exists()).thenReturn(true);
    when(file.isDirectory()).thenReturn(true);
    when(file.delete()).thenReturn(true);

    fileUtils = new FileUtils();
  }

  @Test
  public void shouldDeleteFile() throws Exception {
    when(file.isDirectory()).thenReturn(false);

    fileUtils.deleteDir(file);

    verify(file, atLeastOnce()).delete();
  }

  @Test
  public void shouldDeleteDirectory() throws Exception {
    fileUtils.deleteDir(file);

    verify(file, atLeastOnce()).delete();
  }


  @Test
  public void shouldRecursivelyDeleteFiles() throws Exception {
    File childFiles[] = getFiles(3);

    when(file.listFiles()).thenReturn(childFiles);

    fileUtils.deleteDir(file);

    for (File childFile : childFiles) {
      verify(childFile, atLeastOnce()).delete();
    }
  }

  @Test
  public void shouldRecursivelyDeleteDirectories() throws Exception {
    File[] childFiles = getDirectories(3);

    when(file.listFiles()).thenReturn(childFiles);

    fileUtils.deleteDir(file);

    for (File childFile : childFiles) {
      verify(childFile, atLeastOnce()).delete();
    }
  }

  @Test
  public void shouldThrowWnenUnableToDelete() throws Exception {
    when(file.delete()).thenReturn(false);

    expectedException.expect(IOException.class);
    fileUtils.deleteDir(file);
  }

  @Test
  public void shouldThrowWnenUnableToDeleteChild() throws Exception {
    File childFiles[] = getFiles(3);
    when(childFiles[2].delete()).thenReturn(false);

    when(file.listFiles()).thenReturn(childFiles);

    expectedException.expect(IOException.class);
    fileUtils.deleteDir(file);
  }

  private File[] getDirectories(int number) {
    File[] files = getFiles(number);

    for (File file : files) {
      when(file.isDirectory()).thenReturn(true);
    }

    return files;
  }

  private File[] getFiles(int number) {
    File childFiles[] = new File[number];

    for (int i = 0; i < number; i++) {
      childFiles[i] = mock(File.class);
      when(childFiles[i].exists()).thenReturn(true);
      when(childFiles[i].delete()).thenReturn(true);
    }
    return childFiles;
  }
}