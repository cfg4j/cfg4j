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
package org.cfg4j.source.context.environment;

import static java.util.Objects.requireNonNull;

/**
 * {@link Environment} that never changes.
 */
public class ImmutableEnvironment implements Environment {

  private final String envName;

  /**
   * Construct environment named {@code envName}. This name never changes.
   *
   * @param envName environment name to use
   */
  public ImmutableEnvironment(String envName) {
    this.envName = requireNonNull(envName);
  }

  @Override
  public String getName() {
    return envName;
  }

  @Override
  public String toString() {
    return "ImmutableEnvironment{" +
        "envName='" + envName + '\'' +
        '}';
  }
}
