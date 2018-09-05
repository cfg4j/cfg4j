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
package org.cfg4j.source.context.filesprovider;

import java.io.File;
import java.nio.file.Path;

/**
 * Provides configuration files to use.
 */
public interface ConfigFilesProvider {

  /**
   * Provide a list of configuration files to use.
   * @return {@link Iterable} of configuration {@link File}s to use
   */
  Iterable<Path> getConfigFiles();

}
