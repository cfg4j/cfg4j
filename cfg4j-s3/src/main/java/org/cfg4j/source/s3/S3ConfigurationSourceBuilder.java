package org.cfg4j.source.s3;

import org.cfg4j.source.context.filesprovider.ConfigFilesProvider;
import org.cfg4j.source.context.filesprovider.DefaultConfigFilesProvider;
import org.cfg4j.source.context.propertiesprovider.JsonBasedPropertiesProvider;
import org.cfg4j.source.context.propertiesprovider.PropertiesProviderSelector;
import org.cfg4j.source.context.propertiesprovider.PropertyBasedPropertiesProvider;
import org.cfg4j.source.context.propertiesprovider.YamlBasedPropertiesProvider;

public class S3ConfigurationSourceBuilder {

  private String accessKey;
  private String secretKey;
  private String bucketName;
  private String prefix;
  private ConfigFilesProvider configFilesProvider;
  private PropertiesProviderSelector propertiesProviderSelector;

  public S3ConfigurationSourceBuilder() {
    configFilesProvider = new DefaultConfigFilesProvider();
    propertiesProviderSelector = new PropertiesProviderSelector(
      new PropertyBasedPropertiesProvider(), new YamlBasedPropertiesProvider(), new JsonBasedPropertiesProvider()
    );
  }

  public S3ConfigurationSourceBuilder withCredentials(String accessKey, String secretKey) {
    this.accessKey = accessKey;
    this.secretKey = secretKey;
    return this;
  }

  public S3ConfigurationSourceBuilder withBucketName(String bucketName) {
    this.bucketName = bucketName;
    return this;
  }

  public S3ConfigurationSourceBuilder withPrefix(String prefix) {
    this.prefix = prefix;
    return this;
  }

  public S3ConfigurationSourceBuilder withConfigFilesProvider(ConfigFilesProvider configFilesProvider) {
    this.configFilesProvider = configFilesProvider;
    return this;
  }

  public S3ConfigurationSource build() {
    return new S3ConfigurationSource(accessKey,secretKey,bucketName, prefix,
      configFilesProvider, propertiesProviderSelector);
  }


}
