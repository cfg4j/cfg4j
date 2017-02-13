/**
 * Created by sumeet
 * on 14/2/17.
 */
public class RedisConfigurationSourceBuilder {

  private String host;
  private int port;
  private int database;
  private int connectionTimeout;
  private int socketTimeout;

  public RedisConfigurationSourceBuilder() {
    host = "localhost";
    port = 6379;
    database = 0;
    connectionTimeout = 2000;
    socketTimeout = 2000;
  }

  public RedisConfigurationSourceBuilder withHost(String host) {
    this.host = host;
    return this;
  }

  public RedisConfigurationSourceBuilder withPort(int port) {
    this.port = port;
    return this;
  }

  public RedisConfigurationSourceBuilder withDatabase(int database) {
    this.database = database;
    return this;
  }

  public RedisConfigurationSourceBuilder withConnectionTimeout(int connectionTimeout) {
    this.connectionTimeout = connectionTimeout;
    return this;
  }

  public RedisConfigurationSourceBuilder withSocketTimeout(int socketTimeout) {
    this.socketTimeout = socketTimeout;
    return this;
  }

  public RedisConfigurationSource build() {
    return new RedisConfigurationSource(host, port, database, connectionTimeout, socketTimeout);
  }
}
