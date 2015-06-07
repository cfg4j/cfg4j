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
package pl.nort.config.provider;

import pl.nort.config.source.ConfigurationSource;
import pl.nort.config.source.GitConfigurationSource;
import pl.nort.config.source.context.EnvSelectionStrategy;

/**
 * A factory producing {@link ConfigurationProvider}s.
 */
public class ConfigurationProviders {

  /**
   * A {@link ConfigurationProvider} backed by {@link GitConfigurationSource}.
   *
   * @param repositoryURI a git repository URI (can be remote)
   * @return {@link ConfigurationProvider} using provided git repository as a {@link ConfigurationSource}
   */
  public static ConfigurationProvider backedByGit(String repositoryURI) {
    return new ConfigurationProviderBuilder()
        .withConfigurationSource(new GitConfigurationSource(repositoryURI))
        .build();
  }

  /**
   * A {@link ConfigurationProvider} backed by {@link GitConfigurationSource} and using {@code envSelectionStrategy}
   * to control environment selection.
   *
   * @param repositoryURI        a git repository URI (can be remote)
   * @param envSelectionStrategy {@link EnvSelectionStrategy} to use for determining which environment to choose
   * @return {@link ConfigurationProvider} using provided git repository as a {@link ConfigurationSource}
   */
  public static ConfigurationProvider backedByGit(String repositoryURI, EnvSelectionStrategy envSelectionStrategy) {
    return new ConfigurationProviderBuilder()
        .withConfigurationSource(new GitConfigurationSource(repositoryURI))
        .withEnvSelectionStrategy(envSelectionStrategy)
        .build();
  }

  /**
   * A {@link ConfigurationProvider} backed by a provided {@link ConfigurationSource}.
   *
   * @param source {@link ConfigurationSource} used to supply provider with configuration
   * @return {@link ConfigurationProvider} backed by a {@code source}
   */
  public static ConfigurationProvider withSource(ConfigurationSource source) {
    return new ConfigurationProviderBuilder()
        .withConfigurationSource(source)
        .build();
  }

  /**
   * A {@link ConfigurationProvider} backed by a provided {@link ConfigurationSource} and using {@code envSelectionStrategy}
   * to control environment selection.
   *
   * @param source               {@link ConfigurationSource} used to supply provider with configuration
   * @param envSelectionStrategy {@link EnvSelectionStrategy} to use for determining which environment to choose
   * @return {@link ConfigurationProvider} backed by a {@code source}
   */
  public static ConfigurationProvider withSource(ConfigurationSource source, EnvSelectionStrategy envSelectionStrategy) {
    return new ConfigurationProviderBuilder()
        .withConfigurationSource(source)
        .withEnvSelectionStrategy(envSelectionStrategy)
        .build();
  }

}
