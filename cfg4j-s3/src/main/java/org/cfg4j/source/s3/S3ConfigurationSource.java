package org.cfg4j.source.s3;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import org.cfg4j.source.ConfigurationSource;
import org.cfg4j.source.SourceCommunicationException;
import org.cfg4j.source.context.environment.Environment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

class S3ConfigurationSource implements ConfigurationSource, Closeable {

  private static final Logger LOG = LoggerFactory.getLogger(S3ConfigurationSource.class);

  private boolean initialized;

  private String accessKey;
  private String secretKey;
  private String bucketName;
  private String key;

  private AmazonS3 s3Client;

  S3ConfigurationSource(String accessKey, String secretKey, String bucketName, String key) {
    this.accessKey = accessKey;
    this.secretKey = secretKey;
    this.bucketName = bucketName;
    this.key = key;
    this.initialized = false;
  }

  @Override
  public Properties getConfiguration(Environment environment) {
    if (!initialized) {
      throw new IllegalStateException("Configuration source has to be successfully initialized before you request configuration.");
    }

    GetObjectRequest s3request = new GetObjectRequest(this.bucketName, this.key);
    S3ObjectInputStream is = null;

    try {
      S3Object result = s3Client.getObject(s3request);
      is = result.getObjectContent();
      return inputStreamToProperties(is);
    } catch(IOException e) {
      throw new IllegalStateException("Unable to load configuration from " + bucketName + " " + key, e);
    } finally {
      if (is != null) {
        try {
          is.close();
        } catch (IOException e) {
          LOG.error("Unable to close configuration inputStream", e);
        }
      }
    }
  }

  protected Properties inputStreamToProperties(InputStream is) throws IOException {
    Properties props = new Properties();
    props.load(is);
    return props;
  }

  @Override
  public void init() {
    LOG.info("Initializing " + S3ConfigurationSource.class + " pointing to " + bucketName + key);

    try {
      AWSCredentials awsCredentials = new BasicAWSCredentials(accessKey, secretKey);
      AWSCredentialsProvider provider = new AWSStaticCredentialsProvider(awsCredentials);

      s3Client = AmazonS3ClientBuilder.standard()
        .withCredentials(provider)
        .withRegion(Regions.US_EAST_1)
        .build();

      initialized = true;

    } catch (Exception e) {
      throw new SourceCommunicationException("Unable to connect to s3", e);
    }
  }

  @Override
  public void close() throws IOException {
    initialized = false;
    // TODO ver si hay que hacer algo con s3Client
  }

  @Override
  public String toString() {
    return "S3ConfigurationSource{" +
      "accessKey='" + accessKey + '\'' +
      ", secretKey='" + secretKey + '\'' +
      ", bucketName='" + bucketName + '\'' +
      ", key='" + key + '\'' +
      '}';
  }
}
