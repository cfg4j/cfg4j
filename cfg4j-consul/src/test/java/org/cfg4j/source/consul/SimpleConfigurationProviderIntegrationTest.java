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

package org.cfg4j.source.consul;

import static org.assertj.core.api.Assertions.assertThat;

import com.squareup.okhttp.mockwebserver.Dispatcher;
import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;
import com.squareup.okhttp.mockwebserver.RecordedRequest;
import org.cfg4j.provider.ConfigurationProvider;
import org.cfg4j.provider.ConfigurationProviderBuilder;
import org.cfg4j.source.ConfigurationSource;
import org.cfg4j.source.context.environment.ImmutableEnvironment;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

class SimpleConfigurationProviderIntegrationTest {

  private static final String PING_RESPONSE = "\n" +
      "{\"Config\":{\"Bootstrap\":true,\"BootstrapExpect\":0,\"Server\":true,\"Datacenter\":\"dc1\",\"DataDir\":\"/tmp/consul\",\"DNSRecursor\":\"\",\"DNSRecursors\":[],\"DNSConfig\":{\"NodeTTL\":0,\"ServiceTTL\":null,\"AllowStale\":false,\"EnableTruncate\":false,\"MaxStale\":5000000000,\"OnlyPassing\":false},\"Domain\":\"consul.\",\"LogLevel\":\"INFO\",\"NodeName\":\"receivehead-lm\",\"ClientAddr\":\"127.0.0.1\",\"BindAddr\":\"0.0.0.0\",\"AdvertiseAddr\":\"192.168.0.4\",\"Ports\":{\"DNS\":8600,\"HTTP\":8500,\"HTTPS\":-1,\"RPC\":8400,\"SerfLan\":8301,\"SerfWan\":8302,\"Server\":8300},\"Addresses\":{\"DNS\":\"\",\"HTTP\":\"\",\"HTTPS\":\"\",\"RPC\":\"\"},\"LeaveOnTerm\":false,\"SkipLeaveOnInt\":false,\"StatsiteAddr\":\"\",\"StatsdAddr\":\"\",\"Protocol\":2,\"EnableDebug\":false,\"VerifyIncoming\":false,\"VerifyOutgoing\":false,\"CAFile\":\"\",\"CertFile\":\"\",\"KeyFile\":\"\",\"ServerName\":\"\",\"StartJoin\":[],\"StartJoinWan\":[],\"RetryJoin\":[],\"RetryMaxAttempts\":0,\"RetryIntervalRaw\":\"\",\"RetryJoinWan\":[],\"RetryMaxAttemptsWan\":0,\"RetryIntervalWanRaw\":\"\",\"UiDir\":\"\",\"PidFile\":\"\",\"EnableSyslog\":false,\"SyslogFacility\":\"LOCAL0\",\"RejoinAfterLeave\":false,\"CheckUpdateInterval\":300000000000,\"ACLDatacenter\":\"\",\"ACLTTL\":30000000000,\"ACLTTLRaw\":\"\",\"ACLDefaultPolicy\":\"allow\",\"ACLDownPolicy\":\"extend-cache\",\"Watches\":null,\"DisableRemoteExec\":false,\"DisableUpdateCheck\":false,\"DisableAnonymousSignature\":false,\"HTTPAPIResponseHeaders\":null,\"AtlasInfrastructure\":\"\",\"AtlasJoin\":false,\"Revision\":\"0c7ca91c74587d0a378831f63e189ac6bf7bab3f+CHANGES\",\"Version\":\"0.5.0\",\"VersionPrerelease\":\"\",\"UnixSockets\":{\"Usr\":\"\",\"Grp\":\"\",\"Perms\":\"\"}},\"Member\":{\"Name\":\"receivehead-lm\",\"Addr\":\"192.168.0.4\",\"Port\":8301,\"Tags\":{\"bootstrap\":\"1\",\"build\":\"0.5.0:0c7ca91c\",\"dc\":\"dc1\",\"port\":\"8300\",\"role\":\"consul\",\"vsn\":\"2\",\"vsn_max\":\"2\",\"vsn_min\":\"1\"},\"Status\":1,\"ProtocolMin\":1,\"ProtocolMax\":2,\"ProtocolCur\":2,\"DelegateMin\":2,\"DelegateMax\":4,\"DelegateCur\":4}}";

  private class ModifiableDispatcher extends Dispatcher {

    @Override
    public MockResponse dispatch(RecordedRequest request) {

      switch (request.getPath()) {
        case "/v1/agent/self":
          return new MockResponse().setResponseCode(200).setBody(PING_RESPONSE);
        case "/v1/kv/?recurse=true":
          return new MockResponse()
              .setResponseCode(200)
              .addHeader("Content-Type", "application/json; charset=utf-8")
              .setBody("[{\"CreateIndex\":1,\"ModifyIndex\":1,\"LockIndex\":0,\"Key\":\"us-west-1/featureA.toggle\",\"Flags\":0,\"Value\":\"ZGlzYWJsZWQ=\"}]");
      }
      return new MockResponse().setResponseCode(404);
    }
  }

  private MockWebServer server;
  private ModifiableDispatcher dispatcher;

  @BeforeEach
  void setUp() throws Exception {
    dispatcher = new ModifiableDispatcher();
    runMockServer();
  }

  @AfterEach
  void tearDown() throws Exception {
    server.shutdown();
  }

  @Test
  void readsConfigsFromConsulConfigurationSource() {
    ConfigurationSource source = new ConsulConfigurationSourceBuilder()
        .withHost(server.getHostName())
        .withPort(server.getPort())
        .build();

    ConfigurationProvider provider = new ConfigurationProviderBuilder()
        .withConfigurationSource(source)
        .withEnvironment(new ImmutableEnvironment("us-west-1"))
        .build();

    assertThat(provider.getProperty("featureA.toggle", String.class)).isEqualTo("disabled");
  }


  private void runMockServer() throws IOException {
    server = new MockWebServer();
    server.setDispatcher(dispatcher);
    server.start(0);
  }
}
