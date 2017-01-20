package org.cfg4j.source.s3;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class S3ConfigurationSourceBuilder {
  private Properties properties;
  private String accessKey;
  private String secretKey;
  private String bucketName;

  public S3ConfigurationSourceBuilder withCredentials(String accessKey, String secretKey, String bucketName) {
    this.accessKey = accessKey;
    this.secretKey = secretKey;
    this.bucketName = bucketName;
    return this;
  }

  public S3ConfigurationSourceBuilder withCredentialsFromPropertiesFile(String propFilePath) throws IOException {
    loadProperties(propFilePath);
    this.accessKey = properties.getProperty("aws.s3.accessKey");
    this.secretKey = properties.getProperty("aws.s3.secretKey");
    this.bucketName = properties.getProperty("aws.s3.bucketName");
    return this;
  }

  public S3ConfigurationSource build(AmazonS3Wrapper s3Wrapper) {
    return new S3ConfigurationSource(s3Wrapper);
  }

  public S3ConfigurationSource build() {
    return new S3ConfigurationSource(accessKey, secretKey, bucketName);
  }

  private void loadProperties(String propFileName) throws IOException {
    InputStream is = new FileInputStream(propFileName);
    properties.load(is);
    is.close();
  }
}
