import org.cfg4j.source.context.environment.ImmutableEnvironment;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Properties;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by sumeet
 * on 14/2/17.
 */
@RunWith(MockitoJUnitRunner.class)
public class RedisConfigurationSourceTest {

  @Rule
  public ExpectedException expectedException = ExpectedException.none();

  private RedisConfigurationSource source;
  private Properties properties;

  @Before
  public void setUp() throws Exception {
    source = new RedisConfigurationSourceBuilder()
      .withHost("localhost")
      .withPort(6379)
      .withConnectionTimeout(1000)
      .withSocketTimeout(1000).build();
    source.init();

    properties = new Properties() {{
      put("test-property-1", "1");
      put("test-property-2", "2");
      put("test-property-3", "3");
    }};
  }

  @Test
  public void returnsSourceProperties() throws Exception {
    assertThat(source.getConfiguration(new ImmutableEnvironment("TEST_PROPERTIES"))).isEqualTo(properties);
  }

}