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
import org.cfg4j.source.ConfigurationSource;
import org.cfg4j.source.SourceCommunicationException;
import org.cfg4j.source.context.Environment;

import java.net.URL;
import java.util.Properties;

public class ConsulConfigurationSource implements ConfigurationSource {

  /**
   * Default Consul HTTP API host.
   */
  public static final String DEFAULT_HTTP_HOST = "localhost";

  /**
   * Default Consul HTTP API port.
   */
  public static final int DEFAULT_HTTP_PORT = 8500;

  private final Consul consul;
  private final KeyValueClient kvClient;

  public ConsulConfigurationSource() {
    this(DEFAULT_HTTP_HOST, DEFAULT_HTTP_PORT);
  }

  public ConsulConfigurationSource(URL url) {
    this(url.getHost(), url.getPort());
  }

  private ConsulConfigurationSource(String host, int port) {
    try {
      consul = Consul.newClient(host, port);
      kvClient = consul.keyValueClient();
    } catch (Exception e) {
      throw new SourceCommunicationException("Can't connect to host: " + host + ":" + port, e);
    }
  }

  @Override
  public Properties getConfiguration() {
    return null;
  }

  @Override
  public Properties getConfiguration(Environment environment) {
    return null;
  }

  @Override
  public void refresh() {

  }
}
