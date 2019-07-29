package org.cfg4j.source.zookeeper;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.RetryForever;
import org.cfg4j.source.ConfigurationSource;
import org.cfg4j.source.context.environment.Environment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * Author: secondriver
 * Created: 2019/7/29
 */
public class ZookeeperConfigurationSource implements ConfigurationSource {
  
  private static final Logger LOG = LoggerFactory.getLogger(ZookeeperConfigurationSource.class);
  
  private boolean initialized;
  
  private Map<String, String> zkNodeValues = new HashMap<>();
  
  private final String rootPath;
  
  private final String connectString;
  
  private final DataConverter dataConverter;
  
  private CuratorFramework curatorFramework;
  
  private Environment environment;
  
  public ZookeeperConfigurationSource(String rootPath, String connectString, DataConverter dataConverter) {
    this.rootPath = rootPath;
    this.connectString = connectString;
    this.dataConverter = dataConverter;
  }
  
  @Override
  public Properties getConfiguration(Environment environment) {
    LOG.trace("Requesting configuration for environment: " + environment.getName());
    
    if (!initialized) {
      throw new IllegalStateException("Configuration source has to be successfully initialized before you request configuration.");
    }
    
    this.environment = environment;
    
    reload();
    
    Properties properties = new Properties();
    for (Map.Entry<String, String> entry : this.zkNodeValues.entrySet()) {
      properties.put(entry.getKey(), entry.getValue());
    }
    
    return properties;
  }
  
  @Override
  public void init() {
    try {
      LOG.info("Connect zookeeper  {} ", this.connectString);
      this.curatorFramework = CuratorFrameworkFactory.builder()
        .namespace(this.rootPath.substring(1))
        .connectString(this.connectString)
        .retryPolicy(new RetryForever(3000))
        .build();
      this.curatorFramework.start();
    } catch (Exception e) {
      LOG.error("Can't connect zookeeper {} occur {} . ", this.connectString, e.getMessage());
    }
    this.initialized = true;
  }
  
  private void reload() {
    try {
      String env = this.environment.getName();
      if (!env.startsWith("/")) {
        env = "/" + env;
      }
      List<String> paths = this.curatorFramework.getChildren().forPath(env);
      Map<String, String> newPathValues = new HashMap<>();
      for (String p : paths) {
        byte[] data = this.curatorFramework.getData().forPath(env + "/" + p);
        String value = this.dataConverter.convert(data);
        newPathValues.put(p, value);
      }
      this.zkNodeValues = newPathValues;
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}