/*
 * Copyright 2019 secondriver (secondriver@yeah.net)
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

package org.cfg4j.source.zookeeper;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.RetryOneTime;
import org.apache.curator.test.TestingServer;
import org.assertj.core.util.Lists;
import org.cfg4j.provider.ConfigurationProvider;
import org.cfg4j.provider.ConfigurationProviderBuilder;
import org.cfg4j.provider.GenericType;
import org.cfg4j.source.context.environment.ImmutableEnvironment;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class SimpleConfigurationProviderIntegrationTest {
  
  private TestingServer testingServer;
  
  private CuratorFramework testingClient;
  
  @BeforeEach
  void setUp() throws Exception {
    testingServer = new TestingServer(2182);
    testingClient = CuratorFrameworkFactory.newClient(testingServer.getConnectString(), new RetryOneTime(1000));
    testingClient.start();
    
  }
  
  @AfterEach
  void tearDown() throws Exception {
    testingClient.close();
    testingServer.stop();
  }
  
  @Test
  void readStringConfigurationFromZookeeper() {
    try {
      testingClient.create().forPath("/cfg4j");
      testingClient.create().forPath("/cfg4j/dev");
      testingClient.create().forPath("/cfg4j/dev/name", "Alice".getBytes(StandardCharsets.UTF_8));
      
      ConfigurationProvider provider = new ConfigurationProviderBuilder()
        .withConfigurationSource(new ZookeeperConfigurationSourceBuilder().withConnectString(testingServer.getConnectString()).build())
        .withEnvironment(new ImmutableEnvironment("dev"))
        .build();
      String name = provider.getProperty("name", String.class);
      assertThat(name).isEqualTo("Alice");
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
  
  @Test
  void readIntegerConfigurationFromZookeeper() {
    try {
      testingClient.create().forPath("/cfg4j");
      testingClient.create().forPath("/cfg4j/dev");
      testingClient.create().forPath("/cfg4j/dev/age", String.valueOf(20).getBytes(StandardCharsets.UTF_8));
      
      ConfigurationProvider provider = new ConfigurationProviderBuilder()
        .withConfigurationSource(new ZookeeperConfigurationSourceBuilder().withConnectString(testingServer.getConnectString()).build())
        .withEnvironment(new ImmutableEnvironment("dev"))
        .build();
      Integer age = provider.getProperty("age", Integer.class);
      assertThat(age).isEqualTo(20);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
  
  @Test
  void readListStringConfigurationFromZookeeper() {
    try {
      testingClient.create().forPath("/cfg4j");
      testingClient.create().forPath("/cfg4j/dev");
      testingClient.create().forPath("/cfg4j/dev/hobby", "soccer,ping pong".getBytes(StandardCharsets.UTF_8));
      
      ConfigurationProvider provider = new ConfigurationProviderBuilder()
        .withConfigurationSource(new ZookeeperConfigurationSourceBuilder().withConnectString(testingServer.getConnectString()).build())
        .withEnvironment(new ImmutableEnvironment("dev"))
        .build();
      List<String> hobbys = provider.getProperty("hobby", new GenericType<List<String>>() {
      });
      assertThat(hobbys).containsAll(Lists.list("soccer", "ping pong"));
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
  
  @Test
  void readConfigurationNotExistsFromZookeeper() {
    try {
      testingClient.create().forPath("/cfg4j");
      testingClient.create().forPath("/cfg4j/dev");
      
      ConfigurationProvider provider = new ConfigurationProviderBuilder()
        .withConfigurationSource(new ZookeeperConfigurationSourceBuilder().withConnectString(testingServer.getConnectString()).build())
        .withEnvironment(new ImmutableEnvironment("dev"))
        .build();
      String user = provider.getProperty("user", String.class);
      assertThat(user).isNull();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
  
  @Test
  void readSetPathConfigurationFromZookeeper() {
    try {
      testingClient.create().forPath("/server");
      testingClient.create().forPath("/server/dev");
      String data = "{\n" +
        "  \"server\": \"pet\",\n" +
        "  \"host\": \"192.168.1.2\",\n" +
        "  \"port\": 8080\n" +
        "}";
      testingClient.create().forPath("/server/dev/micro_service", data.getBytes(StandardCharsets.UTF_8));
      
      ConfigurationProvider provider = new ConfigurationProviderBuilder()
        .withConfigurationSource(new ZookeeperConfigurationSourceBuilder()
          .withConnectString(testingServer.getConnectString())
          .withRootPath("/server")
          .build())
        .withEnvironment(new ImmutableEnvironment("dev"))
        .build();
      String serviceInfo = provider.getProperty("micro_service", String.class);
      assertThat(serviceInfo).isEqualTo(data);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
