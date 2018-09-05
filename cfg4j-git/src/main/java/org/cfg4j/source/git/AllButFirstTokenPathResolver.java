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
package org.cfg4j.source.git;

import org.cfg4j.source.context.environment.Environment;

import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.Arrays;

/**
 * Adapter for {@link Environment} to provide git path resolution through {@link PathResolver} interface.
 * The adaptation process works as follows:
 * <ul>
 * <li>the environment name is split into tokens divided by "/"</li>
 * <li>first token is discarded</li>
 * <li>remaining tokens are re-combined and used as a path</li>
 * </ul>
 */
public class AllButFirstTokenPathResolver implements PathResolver {

  @Override
  public Path getPathFor(Environment environment) {
    String[] tokens = environment.getName().split("/");
    return FileSystems.getDefault().getPath("", Arrays.copyOfRange(tokens, 1, tokens.length));
  }
}
