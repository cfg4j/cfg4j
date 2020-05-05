package org.cfg4j.source.s3;

public class S3ConfigurationSourceBuilder {

  private String accessKey;
  private String secretKey;
  private String bucketName;
  private String key;

  public S3ConfigurationSourceBuilder withCredentials(String accessKey, String secretKey) {
    this.accessKey = accessKey;
    this.secretKey = secretKey;
    return this;
  }

  public S3ConfigurationSourceBuilder withBucketName(String bucketName) {
    this.bucketName = bucketName;
    return this;
  }

  public S3ConfigurationSourceBuilder withKey(String key) {
    this.key = key;
    return this;
  }

  public S3ConfigurationSource build() {
    return new S3ConfigurationSource(accessKey,secretKey,bucketName, key);
  }
}
