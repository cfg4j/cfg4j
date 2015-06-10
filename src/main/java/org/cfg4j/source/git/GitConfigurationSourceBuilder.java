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
  private ConfigFilesProvider configFilesProvider;

  /**
   * Construct {@link GitConfigurationSource}s builder
   * <p>
   * Default setup (override using with*() methods)
   * <ul>
   * <li>BranchResolver: {@link EnvironmentBasedBranchResolver}</li>
   * <li>PathResolver: {@link EnvironmentBasedPathResolver}</li>
   * <li>ConfigFilesProvider: {@link DefaultConfigFilesProvider}</li>
   * <li>tmpPath: System.getProperty("java.io.tmpdir")</li>
   * <li>localRepositoryPathInTemp: "cfg4j-config-git-config-repository"</li>
   * </ul>
   */
  public GitConfigurationSourceBuilder() {
    branchResolver = new EnvironmentBasedBranchResolver();
    pathResolver = new EnvironmentBasedPathResolver();
    tmpPath = System.getProperty("java.io.tmpdir");
    localRepositoryPathInTemp = "cfg4j-git-config-repository";
    configFilesProvider = new DefaultConfigFilesProvider();
  }

  /**
   * Set {@link BranchResolver} for {@link GitConfigurationSource}s built by this builder
   *
   * @param branchResolver {@link BranchResolver} to use
   * @return this builder with {@link BranchResolver} set to {@code branchResolver}
   */
  public GitConfigurationSourceBuilder withBranchResolver(BranchResolver branchResolver) {
    this.branchResolver = branchResolver;
    return this;
  }

  /**
   * Set {@link PathResolver} for {@link GitConfigurationSource}s built by this builder
   *
   * @param pathResolver {@link PathResolver} to use
   * @return this builder with {@link PathResolver} set to {@code pathResolver}
   */
  public GitConfigurationSourceBuilder withPathResolver(PathResolver pathResolver) {
    this.pathResolver = pathResolver;
    return this;
  }

  /**
   * Set repository location for {@link GitConfigurationSource}s built by this builder
   *
   * @param repositoryURI repository location to use
   * @return this builder with repository location set to {@code repositoryURI}
   */
  public GitConfigurationSourceBuilder withRepositoryURI(String repositoryURI) {
    this.repositoryURI = repositoryURI;
    return this;
  }

  /**
   * Set temporary dir path for {@link GitConfigurationSource}s built by this builder
   *
   * @param tmpPath temporary dir path to use
   * @return this builder with temporary dir path set to {@code tmpPath}
   */
  public GitConfigurationSourceBuilder withTmpPath(String tmpPath) {
    this.tmpPath = tmpPath;
    return this;
  }

  /**
   * Set relative repository path in temporary dir for {@link GitConfigurationSource}s built by this builder
   *
   * @param localRepositoryPathInTemp relative repository path in temporary dir to use
   * @return this builder with relative repository path in temporary dir set to {@code localRepositoryPathInTemp}
   */
  public GitConfigurationSourceBuilder withLocalRepositoryPathInTemp(String localRepositoryPathInTemp) {
    this.localRepositoryPathInTemp = localRepositoryPathInTemp;
    return this;
  }

  /**
   * Set {@link ConfigFilesProvider} for {@link GitConfigurationSource}s built by this builder
   *
   * @param configFilesProvider {@link ConfigFilesProvider} to use
   * @return this builder with {@link ConfigFilesProvider} set to {@code configFilesProvider}
   */
  public GitConfigurationSourceBuilder withConfigFilesProvider(ConfigFilesProvider configFilesProvider) {
    this.configFilesProvider = configFilesProvider;
    return this;
  }

  /**
   * Build a {@link GitConfigurationSource} using this builder's configuration
   *
   * @return new {@link GitConfigurationSource}
   */
  public GitConfigurationSource build() {
    return new GitConfigurationSource(repositoryURI, tmpPath, localRepositoryPathInTemp, branchResolver, pathResolver,
        configFilesProvider);
  }
}
