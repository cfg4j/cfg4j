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

import org.cfg4j.source.context.filesprovider.ConfigFilesProvider;
import org.cfg4j.source.context.filesprovider.DefaultConfigFilesProvider;
import org.cfg4j.source.context.propertiesprovider.JsonBasedPropertiesProvider;
import org.cfg4j.source.context.propertiesprovider.PropertiesProviderSelector;
import org.cfg4j.source.context.propertiesprovider.PropertyBasedPropertiesProvider;
import org.cfg4j.source.context.propertiesprovider.YamlBasedPropertiesProvider;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Builder for {@link GitConfigurationSource}.
 */
public class GitConfigurationSourceBuilder {

  private BranchResolver branchResolver;
  private PathResolver pathResolver;
  private String repositoryURI;
  private Path tmpPath;
  private String tmpRepoPrefix;
  private ConfigFilesProvider configFilesProvider;
  private PropertiesProviderSelector propertiesProviderSelector;

  /**
   * Construct {@link GitConfigurationSource}s builder
   * <p>
   * Default setup (override using with*() methods)
   * <ul>
   * <li>BranchResolver: {@link FirstTokenBranchResolver}</li>
   * <li>PathResolver: {@link AllButFirstTokenPathResolver}</li>
   * <li>ConfigFilesProvider: {@link DefaultConfigFilesProvider}</li>
   * <li>tmpPath: System.getProperty("java.io.tmpdir")</li>
   * <li>tmpRepoPrefix: "cfg4j-config-git-config-repository"</li>
   * <li>propertiesProviderSelector: {@link PropertiesProviderSelector} with {@link PropertyBasedPropertiesProvider}
   * and {@link YamlBasedPropertiesProvider} providers</li>
   * </ul>
   */
  public GitConfigurationSourceBuilder() {
    branchResolver = new FirstTokenBranchResolver();
    pathResolver = new AllButFirstTokenPathResolver();
    tmpPath = Paths.get(System.getProperty("java.io.tmpdir"));
    tmpRepoPrefix = "cfg4j-git-config-repository";
    configFilesProvider = new DefaultConfigFilesProvider();
    propertiesProviderSelector = new PropertiesProviderSelector(
        new PropertyBasedPropertiesProvider(), new YamlBasedPropertiesProvider(), new JsonBasedPropertiesProvider()
    );
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
  public GitConfigurationSourceBuilder withTmpPath(Path tmpPath) {
    this.tmpPath = tmpPath;
    return this;
  }

  /**
   * Set relative repository path in temporary dir for {@link GitConfigurationSource}s built by this builder
   *
   * @param tmpRepoPrefix relative repository path in temporary dir to use
   * @return this builder with relative repository path in temporary dir set to {@code tmpRepoPrefix}
   */
  public GitConfigurationSourceBuilder withTmpRepoPrefix(String tmpRepoPrefix) {
    this.tmpRepoPrefix = tmpRepoPrefix;
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
    return new GitConfigurationSource(repositoryURI, tmpPath, tmpRepoPrefix, branchResolver, pathResolver,
        configFilesProvider, propertiesProviderSelector);
  }

  @Override
  public String toString() {
    return "GitConfigurationSourceBuilder{" +
        "branchResolver=" + branchResolver +
        ", pathResolver=" + pathResolver +
        ", repositoryURI='" + repositoryURI + '\'' +
        ", tmpPath='" + tmpPath + '\'' +
        ", tmpRepoPrefix='" + tmpRepoPrefix + '\'' +
        ", configFilesProvider=" + configFilesProvider +
        '}';
  }
}
