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
package pl.nort.config.source.git;

import static com.google.common.base.Preconditions.checkNotNull;

import pl.nort.config.source.context.Environment;

import java.util.StringJoiner;

/**
 * Adapter for {@link Environment} to provide git path resolution through {@link PathResolver} interface.
 * If underlying {@link Environment} name changes the {@link #getPath()} will reflect that change immediately.
 * The adaptation process works as follows:
 * <ul>
 * <li>the environment name is split into tokens divided by "/"</li>
 * <li>first token is discarded</li>
 * <li>remaining tokens are re-combined and used as a path</li>
 * </ul>
 */
public class EnvironmentBasedPathResolver implements PathResolver {

  private final Environment environment;

  public EnvironmentBasedPathResolver(Environment environment) {
    this.environment = checkNotNull(environment);
  }

  @Override
  public String getPath() {
    String[] tokens = environment.getName().split("/");

    StringJoiner stringJoiner = new StringJoiner("/");

    for (int i = 1; i < tokens.length; i++) {
      stringJoiner.add(tokens[i]);
    }

    return stringJoiner.toString();
  }
}
