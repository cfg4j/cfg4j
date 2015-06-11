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

import static org.assertj.core.api.Assertions.assertThat;

import com.squareup.okhttp.mockwebserver.Dispatcher;
import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;
import com.squareup.okhttp.mockwebserver.RecordedRequest;
import org.cfg4j.source.SourceCommunicationException;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.net.URL;
import java.util.concurrent.TimeUnit;


@RunWith(MockitoJUnitRunner.class)
public class ConsulConfigurationSourceIntegrationTest {

  private static final String PING_RESPONSE = "\n" +
      "{\"Config\":{\"Bootstrap\":true,\"BootstrapExpect\":0,\"Server\":true,\"Datacenter\":\"dc1\",\"DataDir\":\"/tmp/consul\",\"DNSRecursor\":\"\",\"DNSRecursors\":[],\"DNSConfig\":{\"NodeTTL\":0,\"ServiceTTL\":null,\"AllowStale\":false,\"EnableTruncate\":false,\"MaxStale\":5000000000,\"OnlyPassing\":false},\"Domain\":\"consul.\",\"LogLevel\":\"INFO\",\"NodeName\":\"receivehead-lm\",\"ClientAddr\":\"127.0.0.1\",\"BindAddr\":\"0.0.0.0\",\"AdvertiseAddr\":\"192.168.0.4\",\"Ports\":{\"DNS\":8600,\"HTTP\":8500,\"HTTPS\":-1,\"RPC\":8400,\"SerfLan\":8301,\"SerfWan\":8302,\"Server\":8300},\"Addresses\":{\"DNS\":\"\",\"HTTP\":\"\",\"HTTPS\":\"\",\"RPC\":\"\"},\"LeaveOnTerm\":false,\"SkipLeaveOnInt\":false,\"StatsiteAddr\":\"\",\"StatsdAddr\":\"\",\"Protocol\":2,\"EnableDebug\":false,\"VerifyIncoming\":false,\"VerifyOutgoing\":false,\"CAFile\":\"\",\"CertFile\":\"\",\"KeyFile\":\"\",\"ServerName\":\"\",\"StartJoin\":[],\"StartJoinWan\":[],\"RetryJoin\":[],\"RetryMaxAttempts\":0,\"RetryIntervalRaw\":\"\",\"RetryJoinWan\":[],\"RetryMaxAttemptsWan\":0,\"RetryIntervalWanRaw\":\"\",\"UiDir\":\"\",\"PidFile\":\"\",\"EnableSyslog\":false,\"SyslogFacility\":\"LOCAL0\",\"RejoinAfterLeave\":false,\"CheckUpdateInterval\":300000000000,\"ACLDatacenter\":\"\",\"ACLTTL\":30000000000,\"ACLTTLRaw\":\"\",\"ACLDefaultPolicy\":\"allow\",\"ACLDownPolicy\":\"extend-cache\",\"Watches\":null,\"DisableRemoteExec\":false,\"DisableUpdateCheck\":false,\"DisableAnonymousSignature\":false,\"HTTPAPIResponseHeaders\":null,\"AtlasInfrastructure\":\"\",\"AtlasJoin\":false,\"Revision\":\"0c7ca91c74587d0a378831f63e189ac6bf7bab3f+CHANGES\",\"Version\":\"0.5.0\",\"VersionPrerelease\":\"\",\"UnixSockets\":{\"Usr\":\"\",\"Grp\":\"\",\"Perms\":\"\"}},\"Member\":{\"Name\":\"receivehead-lm\",\"Addr\":\"192.168.0.4\",\"Port\":8301,\"Tags\":{\"bootstrap\":\"1\",\"build\":\"0.5.0:0c7ca91c\",\"dc\":\"dc1\",\"port\":\"8300\",\"role\":\"consul\",\"vsn\":\"2\",\"vsn_max\":\"2\",\"vsn_min\":\"1\"},\"Status\":1,\"ProtocolMin\":1,\"ProtocolMax\":2,\"ProtocolCur\":2,\"DelegateMin\":2,\"DelegateMax\":4,\"DelegateCur\":4}}";

  @Rule
  public ExpectedException expectedException = ExpectedException.none();

  private MockWebServer server;

  private final Dispatcher dispatcher = new Dispatcher() {

    @Override
    public MockResponse dispatch(RecordedRequest request) throws InterruptedException {

      switch (request.getPath()) {
        case "/v1/agent/self":
          return new MockResponse().setResponseCode(200).setBody(PING_RESPONSE);
        case "/v1/":
          return new MockResponse().setResponseCode(200).setBody("version=9");
        case "/v1/abc":
          return new MockResponse().setResponseCode(200).setBody("{\\\"info\\\":{\\\"name\":\"Lucas Albuquerque\",\"age\":\"21\",\"gender\":\"male\"}}");
      }
      return new MockResponse().setResponseCode(404);
    }
  };

  private ConsulConfigurationSource source;


  @Before
  public void setUp() throws Exception {
    server = new MockWebServer();
    server.setDispatcher(dispatcher);
    server.start(ConsulConfigurationSource.DEFAULT_HTTP_PORT);
    source = new ConsulConfigurationSource(server.getUrl(""));
  }

  @After
  public void tearDown() throws Exception {
    server.shutdown();
  }

  @Test
  public void shouldConnectToLocalhostAgentByDefault() throws Exception {
    RecordedRequest request = server.takeRequest(0, TimeUnit.MILLISECONDS);
    assertThat(request).isNotNull();
  }

  @Test
  public void shouldConnectToSpecifiedAgent() throws Exception {

  }

  @Test
  public void shouldThrowOnConnectionFailure() throws Exception {
    server.shutdown();
    expectedException.expect(SourceCommunicationException.class);
    source = new ConsulConfigurationSource();
  }

  @Test
  public void shouldThrowOnConnectionFailure2() throws Exception {
    expectedException.expect(SourceCommunicationException.class);
    source = new ConsulConfigurationSource(new URL("http", "localhost", ConsulConfigurationSource.DEFAULT_HTTP_PORT + 1, ""));
  }

  @Test
  public void getPropertiesShouldReturnAllKeys() throws Exception {

  }

  @Test
  public void getPropertiesShouldBeUpdatedByRefresh() throws Exception {

  }

  @Test
  public void getPropertiesShouldReturnAllKeysFromGivenContext() throws Exception {

  }

  @Test
  public void getProperties2ShouldBeUpdatedByRefresh() throws Exception {

  }

  @Test
  public void refreshShouldThrowOnConnectionFailure() throws Exception {

  }
}