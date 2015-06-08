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
package org.cfg4j.source.git;

/**
 * Builder for {@link GitConfigurationSource}.
 */
public class GitConfigurationSourceBuilder {

  private BranchResolver branchResolver;
  private PathResolver pathResolver;
  private String repositoryURI;
  private String tmpPath;
  private String localRepositoryPathInTemp;

  public GitConfigurationSourceBuilder() {
    branchResolver = new EnvironmentBasedBranchResolver();
    pathResolver = new EnvironmentBasedPathResolver();
    tmpPath = System.getProperty("java.io.tmpdir");
    localRepositoryPathInTemp = "nort-config-git-config-repository";
  }

  public GitConfigurationSourceBuilder withBranchResolver(BranchResolver branchResolver) {
    this.branchResolver = branchResolver;
    return this;
  }

  public GitConfigurationSourceBuilder withPathResolver(PathResolver pathResolver) {
    this.pathResolver = pathResolver;
    return this;
  }

  public GitConfigurationSourceBuilder withRepositoryURI(String repositoryURI) {
    this.repositoryURI = repositoryURI;
    return this;
  }

  public GitConfigurationSourceBuilder withTmpPath(String tmpPath) {
    this.tmpPath = tmpPath;
    return this;
  }

  public GitConfigurationSourceBuilder withLocalRepositoryPathInTemp(String localRepositoryPathInTemp) {
    this.localRepositoryPathInTemp = localRepositoryPathInTemp;
    return this;
  }

  public GitConfigurationSource build() {
    return new GitConfigurationSource(repositoryURI, tmpPath, localRepositoryPathInTemp, branchResolver, pathResolver);
  }
}
