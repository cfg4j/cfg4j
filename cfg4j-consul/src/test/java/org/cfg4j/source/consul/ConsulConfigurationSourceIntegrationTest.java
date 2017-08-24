/*
 * Copyright 2015-2016 Norbert Potocki (norbert.potocki@nort.pl)
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
import org.assertj.core.data.MapEntry;
import org.cfg4j.source.SourceCommunicationException;
import org.cfg4j.source.context.environment.DefaultEnvironment;
import org.cfg4j.source.context.environment.Environment;
import org.cfg4j.source.context.environment.ImmutableEnvironment;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.IOException;
import java.util.concurrent.TimeUnit;


@RunWith(MockitoJUnitRunner.class)
public class ConsulConfigurationSourceIntegrationTest {

  private static final String PING_RESPONSE = "\n" +
      "{\"Config\":{\"Bootstrap\":true,\"BootstrapExpect\":0,\"Server\":true,\"Datacenter\":\"dc1\",\"DataDir\":\"/tmp/consul\",\"DNSRecursor\":\"\",\"DNSRecursors\":[],\"DNSConfig\":{\"NodeTTL\":0,\"ServiceTTL\":null,\"AllowStale\":false,\"EnableTruncate\":false,\"MaxStale\":5000000000,\"OnlyPassing\":false},\"Domain\":\"consul.\",\"LogLevel\":\"INFO\",\"NodeName\":\"receivehead-lm\",\"ClientAddr\":\"127.0.0.1\",\"BindAddr\":\"0.0.0.0\",\"AdvertiseAddr\":\"192.168.0.4\",\"Ports\":{\"DNS\":8600,\"HTTP\":8500,\"HTTPS\":-1,\"RPC\":8400,\"SerfLan\":8301,\"SerfWan\":8302,\"Server\":8300},\"Addresses\":{\"DNS\":\"\",\"HTTP\":\"\",\"HTTPS\":\"\",\"RPC\":\"\"},\"LeaveOnTerm\":false,\"SkipLeaveOnInt\":false,\"StatsiteAddr\":\"\",\"StatsdAddr\":\"\",\"Protocol\":2,\"EnableDebug\":false,\"VerifyIncoming\":false,\"VerifyOutgoing\":false,\"CAFile\":\"\",\"CertFile\":\"\",\"KeyFile\":\"\",\"ServerName\":\"\",\"StartJoin\":[],\"StartJoinWan\":[],\"RetryJoin\":[],\"RetryMaxAttempts\":0,\"RetryIntervalRaw\":\"\",\"RetryJoinWan\":[],\"RetryMaxAttemptsWan\":0,\"RetryIntervalWanRaw\":\"\",\"UiDir\":\"\",\"PidFile\":\"\",\"EnableSyslog\":false,\"SyslogFacility\":\"LOCAL0\",\"RejoinAfterLeave\":false,\"CheckUpdateInterval\":300000000000,\"ACLDatacenter\":\"\",\"ACLTTL\":30000000000,\"ACLTTLRaw\":\"\",\"ACLDefaultPolicy\":\"allow\",\"ACLDownPolicy\":\"extend-cache\",\"Watches\":null,\"DisableRemoteExec\":false,\"DisableUpdateCheck\":false,\"DisableAnonymousSignature\":false,\"HTTPAPIResponseHeaders\":null,\"AtlasInfrastructure\":\"\",\"AtlasJoin\":false,\"Revision\":\"0c7ca91c74587d0a378831f63e189ac6bf7bab3f+CHANGES\",\"Version\":\"0.5.0\",\"VersionPrerelease\":\"\",\"UnixSockets\":{\"Usr\":\"\",\"Grp\":\"\",\"Perms\":\"\"}},\"Member\":{\"Name\":\"receivehead-lm\",\"Addr\":\"192.168.0.4\",\"Port\":8301,\"Tags\":{\"bootstrap\":\"1\",\"build\":\"0.5.0:0c7ca91c\",\"dc\":\"dc1\",\"port\":\"8300\",\"role\":\"consul\",\"vsn\":\"2\",\"vsn_max\":\"2\",\"vsn_min\":\"1\"},\"Status\":1,\"ProtocolMin\":1,\"ProtocolMax\":2,\"ProtocolCur\":2,\"DelegateMin\":2,\"DelegateMax\":4,\"DelegateCur\":4}}";

  private class ModifiableDispatcher extends Dispatcher {

    private static final String disabledBase64 = "ZGlzYWJsZWQ=";
    private static final String enabledBase64 = "ZW5hYmxlZA==";

		private static final String usWest1Config = "{\"CreateIndex\":1,\"ModifyIndex\":1,\"LockIndex\":0,\"Key\":\"us-west-1/featureA.toggle\",\"Flags\":0,\"Value\":\"" + disabledBase64 + "\"}";
		private static final String usWest2FeatureEnable = "{\"CreateIndex\":2,\"ModifyIndex\":2,\"LockIndex\":0,\"Key\":\"us-west-2/featureB.toggle\",\"Flags\":0,\"Value\":\"" + enabledBase64 + "\"}";
		private static final String usWest2FeatureDisabled = "{\"CreateIndex\":2,\"ModifyIndex\":2,\"LockIndex\":0,\"Key\":\"us-west-2/featureB.toggle\",\"Flags\":0,\"Value\":\"" + disabledBase64 + "\"}";

    private boolean usWest2Toggle = false;

    void toggleUsWest2() {
      usWest2Toggle = !usWest2Toggle;
    }

		@Override
		public MockResponse dispatch(RecordedRequest request) throws InterruptedException {
			StringBuilder sbResponseBody = new StringBuilder();
			switch (request.getPath()) {
				case "/v1/agent/self":
					return new MockResponse().setResponseCode(200).setBody(PING_RESPONSE);
				case "/v1/kv/?recurse=true":
					sbResponseBody.append("[");
					sbResponseBody.append(usWest1Config).append(",");
					if(usWest2Toggle) {
						sbResponseBody.append(usWest2FeatureEnable);
					} else {
						sbResponseBody.append(usWest2FeatureDisabled);
					}
					sbResponseBody.append("]");

					return new MockResponse()
						.setResponseCode(200)
						.addHeader("Content-Type", "application/json; charset=utf-8")
						.setBody(sbResponseBody.toString());

				case "/v1/kv/us-west-1?recurse=true":
					return new MockResponse()
						.setResponseCode(200)
						.addHeader("Content-Type", "application/json; charset=utf-8")
						.setBody("[" + usWest1Config + "]");
			}
			return new MockResponse().setResponseCode(404);
		}
  }

  @Rule
  public ExpectedException expectedException = ExpectedException.none();
  private MockWebServer server;
  private ConsulConfigurationSource source;
  private ModifiableDispatcher dispatcher;


  @Before
  public void setUp() throws Exception {
    dispatcher = new ModifiableDispatcher();
    runMockServer();
    source = new ConsulConfigurationSourceBuilder()
        .withHost(server.getHostName())
        .withPort(server.getPort())
        .build();

    source.init();
  }

  @After
  public void tearDown() throws Exception {
    server.shutdown();
  }

  @Test
  public void connectsToSpecifiedAgent() throws Exception {
    RecordedRequest request = server.takeRequest(0, TimeUnit.MILLISECONDS);
    assertThat(request).isNotNull();
  }

  @Test
  public void initThrowsOnConnectionFailure() throws Exception {
    server.shutdown();
    source = new ConsulConfigurationSourceBuilder()
        .withHost(server.getHostName())
        .withPort(server.getPort())
        .build();

    expectedException.expect(SourceCommunicationException.class);
    source.init();
  }

  @Test
  public void getConfigurationReturnsAllKeysFromGivenEnvironment() throws Exception {
    Environment environment = new ImmutableEnvironment("us-west-1");

    assertThat(source.getConfiguration(environment)).contains(MapEntry.entry("featureA.toggle", "disabled"));
  }

  @Test
  public void getConfigurationIgnoresLeadingSlashInGivenEnvironment() throws Exception {
    Environment environment = new ImmutableEnvironment("/us-west-1");

    assertThat(source.getConfiguration(environment)).contains(MapEntry.entry("featureA.toggle", "disabled"));
  }

  @Test
	public void getConfigurationWithDefaultEnvironmentReturnsAllKeys() {
  	Environment environment = new DefaultEnvironment();

		assertThat(source.getConfiguration(environment)).contains(MapEntry.entry("us-west-1.featureA.toggle", "disabled"));
		assertThat(source.getConfiguration(environment)).contains(MapEntry.entry("us-west-2.featureB.toggle", "disabled"));
	}

  @Test
  public void getConfigurationThrowsBeforeInitCalled() throws Exception {
    source = new ConsulConfigurationSourceBuilder()
        .withHost(server.getHostName())
        .withPort(server.getPort())
        .build();

    expectedException.expect(IllegalStateException.class);
    source.getConfiguration(new ImmutableEnvironment(""));
  }

  @Test
  public void getConfigurationThrowsAfterFailedReload() throws Exception {
    server.shutdown();
    try {
      source.getConfiguration(new ImmutableEnvironment("us-west-2"));
    } catch (Exception e) {
      // NOP
    }

    expectedException.expect(SourceCommunicationException.class);
    source.getConfiguration(new ImmutableEnvironment(""));
  }

  private void runMockServer() throws IOException {
    server = new MockWebServer();
    server.setDispatcher(dispatcher);
    server.start(0);
  }
}