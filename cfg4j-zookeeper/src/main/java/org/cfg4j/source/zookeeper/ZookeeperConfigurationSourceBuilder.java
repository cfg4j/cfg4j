package org.cfg4j.source.zookeeper;

import java.nio.charset.StandardCharsets;

/**
 * Author: secondriver
 * Created: 2019/7/29
 */
public class ZookeeperConfigurationSourceBuilder {
  
  private String rootPath = "/cf4j";
  
  private String connectString = "localhost:2181";
  
  private DataConverter dataConverter = data -> new String(data, StandardCharsets.UTF_8);
  
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
  
  public ZookeeperConfigurationSourceBuilder withConnectString(String connectString) {
    if (connectString == null || "".equals(connectString.trim())) {
      throw new IllegalStateException("Zookeeper connection string can't be null or empty.");
    }
    this.connectString = connectString;
    return this;
  }
  
  public ZookeeperConfigurationSourceBuilder withDataConvert(DataConverter dataConverter) {
    if (dataConverter == null) {
      throw new IllegalArgumentException("DataConverter must be not null.");
    }
    this.dataConverter = dataConverter;
    return this;
  }
  
  public ZookeeperConfigurationSource build() {
    return new ZookeeperConfigurationSource(this.rootPath, this.connectString, this.dataConverter);
  }
}
