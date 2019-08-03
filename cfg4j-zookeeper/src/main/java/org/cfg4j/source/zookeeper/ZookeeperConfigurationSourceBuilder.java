/*
 *  Copyright 2019 secondriver (secondriver@yeah.net)
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package org.cfg4j.source.zookeeper;

import java.nio.charset.StandardCharsets;


/**
 * Builder for {@link ZookeeperConfigurationSource}.
 */
public class ZookeeperConfigurationSourceBuilder {
  
  /**
   * Storage configuration root ZNode path
   * <p>
   * default value '/cfg4j'
   */
  private String rootPath = "/cfg4j";
  
  /**
   * Connect Zookeeper server url
   * <p>
   * default value 'localhost:2181'
   */
  private String connectString = "localhost:2181";
  
  /**
   * ZNode byte[] data convert to String
   * <p>
   * default byte[] use UTF-8 convert to String type
   */
  private DataConverter dataConverter = data -> new String(data, StandardCharsets.UTF_8);
  
  
  /**
   * Set rootPath for {@link ZookeeperConfigurationSource}s built by this builder
   *
   * @param rootPath storage configuration root ZNode path
   * @return this builder with ZNode rootPath set to provided parameter
   */
  public ZookeeperConfigurationSourceBuilder withRootPath(String rootPath) {
    if (rootPath != null && !"".equals(rootPath.trim())) {
      this.rootPath = rootPath;
    }
    if (rootPath == null || "".equals(rootPath.trim())) {
      this.rootPath = "/";
    }
    if (!this.rootPath.startsWith("/")) {
      this.rootPath = "/" + this.rootPath;
    }
    return this;
  }
  
  /**
   * Set connectString for {@link ZookeeperConfigurationSource}s built by this builder
   *
   * @param connectString connect zookeeper server url
   * @return this builder with connectString set to provided parameter
   */
  public ZookeeperConfigurationSourceBuilder withConnectString(String connectString) {
    if (connectString == null || "".equals(connectString.trim())) {
      throw new IllegalStateException("Zookeeper connection string can't be null or empty.");
    }
    this.connectString = connectString;
    return this;
  }
  
  /**
   * Set dataConverter for {@link ZookeeperConfigurationSource}s built by this builder
   *
   * @param dataConverter ZNode byte[] data convert to String
   * @return this builder with dataConverter set to provided parameter
   */
  public ZookeeperConfigurationSourceBuilder withDataConvert(DataConverter dataConverter) {
    if (dataConverter == null) {
      throw new IllegalArgumentException("DataConverter must be not null.");
    }
    this.dataConverter = dataConverter;
    return this;
  }
  
  /**
   * Build a {@link ZookeeperConfigurationSource} using this builder's configuration
   *
   * @return new {@link ZookeeperConfigurationSource}
   */
  public ZookeeperConfigurationSource build() {
    return new ZookeeperConfigurationSource(this.rootPath, this.connectString, this.dataConverter);
  }
}
