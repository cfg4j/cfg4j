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
package org.cfg4j.source.consul;

import com.orbitz.consul.Consul;
import com.orbitz.consul.KeyValueClient;
import com.orbitz.consul.model.kv.Value;
import org.cfg4j.source.ConfigurationSource;
import org.cfg4j.source.SourceCommunicationException;
import org.cfg4j.source.context.Environment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class ConsulConfigurationSource implements ConfigurationSource {

  private static final Logger LOG = LoggerFactory.getLogger(ConsulConfigurationSource.class);

  /**
   * Default Consul HTTP API host.
   */
  public static final String DEFAULT_HTTP_HOST = "localhost";

  /**
   * Default Consul HTTP API port.
   */
  public static final int DEFAULT_HTTP_PORT = 8500;

  private final KeyValueClient kvClient;
  private Map<String, String> consulValues;

  public ConsulConfigurationSource() {
    this(DEFAULT_HTTP_HOST, DEFAULT_HTTP_PORT);
  }

  public ConsulConfigurationSource(URL url) {
    this(url.getHost(), url.getPort());
  }

  private ConsulConfigurationSource(String host, int port) {
    try {
      LOG.info("Connecting to Consul client at " + host + ":" + port);

      Consul consul = Consul.newClient(host, port);
      kvClient = consul.keyValueClient();
    } catch (Exception e) {
      throw new SourceCommunicationException("Can't connect to host: " + host + ":" + port, e);
    }

    refresh();
  }

  @Override
  public Properties getConfiguration(Environment environment) {
    LOG.trace("Requesting configuration for environment: " + environment.getName());

    Properties properties = new Properties();
    String path = environment.getName();

    if (path.startsWith("/")) {
      path = path.substring(1);
    }

    if (path.length() > 0 && !path.endsWith("/")) {
      path = path + "/";
    }

    for (Map.Entry<String, String> entry : consulValues.entrySet()) {
      if (entry.getKey().startsWith(path)) {
        properties.put(entry.getKey().substring(path.length()).replace("/", "."), entry.getValue());
      }
    }

    return properties;
  }

  @Override
  public void refresh() {
    consulValues = new HashMap<>();
    List<Value> valueList;

    try {
      LOG.debug("Refreshing configuration from Consuls' K-V store");
      valueList = kvClient.getValues("/");
    } catch (Exception e) {
      throw new SourceCommunicationException("Can't get values from k-v store", e);
    }

    for (Value value : valueList) {
      String val = new String(Base64.getDecoder().decode(value.getValue().getBytes(StandardCharsets.UTF_8)));

      LOG.trace("Consul provided configuration key: " + value.getKey() + " with value: " + val);

      consulValues.put(value.getKey(), val);
    }
  }
}
