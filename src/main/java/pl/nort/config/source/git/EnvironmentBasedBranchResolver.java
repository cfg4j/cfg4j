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

/**
 * Adapter for {@link Environment} to provide git branch resolution through {@link BranchResolver} interface.
 * If underlying {@link Environment} name changes the {@link #branchName()} will reflect that change immediately.
 * The adaptation process works as follows:
 * <ul>
 * <li>the environment name is split into tokens divided by "/"</li>
 * <li>first token is treated as a branch name</li>
 * <li>if the branch name is empty ("", or contains only whitespaces) then the "master" branch is used</li>
 * </ul>
 */
public class EnvironmentBasedBranchResolver implements BranchResolver {

  private final Environment environment;

  public EnvironmentBasedBranchResolver(Environment environment) {
    this.environment = checkNotNull(environment);
  }

  @Override
  public String branchName() {
    String[] tokens = environment.getName().split("/");

    String branchName = tokens[0].trim();
    if (branchName.isEmpty()) {
      branchName = "master";
    }

    return branchName;
  }
}
