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
package pl.nort.config.source.context;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * {@link EnvSelectionStrategy} that never changes
 */
public class ImmutableEnvSelectionStrategy implements EnvSelectionStrategy {

  private final String envName;

  public ImmutableEnvSelectionStrategy(String envName) {
    this.envName = checkNotNull(envName);
  }

  @Override
  public String getEnvironmentName() {
    return envName;
  }
}
