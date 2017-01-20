package org.cfg4j.source.s3;

import com.amazonaws.services.kms.model.NotFoundException;
import com.amazonaws.services.s3.model.S3Object;
import org.cfg4j.source.ConfigurationSource;
import org.cfg4j.source.context.environment.Environment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.Properties;

public class S3ConfigurationSource implements ConfigurationSource {

  private static final Logger LOG = LoggerFactory.getLogger(S3ConfigurationSource.class);
  private boolean initialized = false;
  private AmazonS3Wrapper s3wrapper;

  public S3ConfigurationSource(AmazonS3Wrapper s3wrapper){
    this.s3wrapper = s3wrapper;
  }

  public S3ConfigurationSource(String accessKey, String secretKey, String bucketName) {
    this.s3wrapper = new AmazonS3WrapperImpl(accessKey, secretKey, bucketName);
  }

  @Override
  public Properties getConfiguration(Environment environment) {
    LOG.trace("Requesting configuration for environment: " + environment.getName());

    if (!initialized) {
      throw new IllegalStateException("Configuration source has to be successfully initialized before you request configuration.");
    }

    if(environment == null || environment.getName() == null || environment.getName().isEmpty()){
      throw new IllegalArgumentException("Environment must not be null or empty");
    }

    if (!s3wrapper.isFileExist(environment.getName())) {
      throw new NotFoundException(MessageFormat.format("File name {0} does not exist", environment.getName()));
    }

    S3Object fileFromS3 = s3wrapper.getFile(environment.getName());

    ///
    //String content = s3wrapper.getFileContent(fileFromS3);
    //System.out.print(content);
    ///

    Properties propertiesToReturn = new Properties();
    try (InputStream input = fileFromS3.getObjectContent()) {
      propertiesToReturn.load(input);
    } catch (IOException e) {
      throw new IllegalStateException("Unable to load configuration from " + environment.getName() + " file", e);
    }
    finally {
      try {
        fileFromS3.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

    return propertiesToReturn;
  }

  @Override
  public void init() {
    LOG.info("Connecting to s3...");
    s3wrapper.init();
    if (!s3wrapper.isBucketExist(getBucketName())) {
      throw new ExceptionInInitializerError(MessageFormat.format("Given bucket ''{0}'' does not exist", getBucketName()));
    }

    initialized = true;
  }

  public String getBucketName() {
    return s3wrapper.getBucketName();
  }

  public void setBucketName(String bucketName) {
    s3wrapper.setBucketName(bucketName);
  }
}

