import org.cfg4j.source.ConfigurationSource;
import org.cfg4j.source.SourceCommunicationException;
import org.cfg4j.source.context.environment.Environment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;

import java.util.Map;
import java.util.Properties;
import java.util.Set;

/**
 * Created by sumeet
 * on 13/2/17.
 */
public class RedisConfigurationSource implements ConfigurationSource {

  private static final Logger LOG = LoggerFactory.getLogger(RedisConfigurationSource.class);

  private String host;
  private int port;
  private int database;
  private int connectionTimeout;
  private int socketTimeout;
  private Jedis jedis = null;

  public RedisConfigurationSource(String host, int port, int database, int connectionTimeout, int socketTimeout) {
    this.host = host;
    this.port = port;
    this.database = database;
    this.connectionTimeout = connectionTimeout;
    this.socketTimeout = socketTimeout;
  }

  private Properties getPropertiesFromRedis(String redisKey) {
    LOG.trace("GETTING_PROPERTIES_FROM_REDIS");
    Map<String, String> propertiesFromRedis = jedis.hgetAll(redisKey);

    LOG.info(propertiesFromRedis.size() + "_PROPERTIES_FETCHED_FROM_REDIS");

    Properties properties = new Properties();
    Set<Map.Entry<String, String>> entries = propertiesFromRedis.entrySet();
    for (Map.Entry<String, String> entry : entries) {
      properties.setProperty(entry.getKey(), entry.getValue());
    }
    return properties;
  }

  @Override
  public Properties getConfiguration(Environment environment) {

    if (!Util.isSet(jedis)) {
      throw new IllegalStateException("CONFIGURATION_SOURCE_UNINITIALISED");
    }

    String redisKey = environment.getName();
    Properties properties = getPropertiesFromRedis(redisKey);
    return properties;
  }

  @Override
  public void init() {
    LOG.info("ESTABLISHING_CONNECTION_TO_REDIS|" + host + "|" + port);
    try {
      jedis = new Jedis(host, port, connectionTimeout, socketTimeout);
    } catch (Exception e) {
      throw new SourceCommunicationException("CANNOT_CONNECT_TO_REDIS|" + host + "|" + port, e);
    }
  }
}
