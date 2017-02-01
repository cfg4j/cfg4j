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

/**
 * Note: use {@link S3ConfigurationSourceBuilder} for building instances of this class.
 * <p>
 *     Read configuration files from AWS S3.
 * </p>
 */
public class S3ConfigurationSource implements ConfigurationSource {

  private static final Logger LOG = LoggerFactory.getLogger(S3ConfigurationSource.class);
  private boolean initialized = false;
  private AmazonS3Wrapper s3wrapper;

  /**
   * Note: use {@link S3ConfigurationSourceBuilder} for building instances of this class.
   * <p>
   *     Read configuration for an AWS S3 bucket and credentials defined in the {@code AmazonS3Wrapper}
   * </p>
   * @param s3wrapper Pre-configured wrapper for the S3 service
   */
  public S3ConfigurationSource(AmazonS3Wrapper s3wrapper){
    this.s3wrapper = s3wrapper;
  }


  /**
   * Note: use {@link S3ConfigurationSourceBuilder} for building instances of this class.
   * @param accessKey An API access key for your AWS account
   * @param secretKey An API secret key for your AWS account
   * @param bucketName The name of the bucket where configuration files are located
   */
  public S3ConfigurationSource(String accessKey, String secretKey, String bucketName) {
    this.s3wrapper = new AmazonS3WrapperImpl(accessKey, secretKey, bucketName);
  }

  /**
   * Get the configuration properties for the corresponding environment from S3
   * @param environment environment to use
   * @return A {@link Properties} collection extracted from the configuration file
   *
   * @throws IllegalStateException when {@code init()} is not called prior to calling {@code getConfiguration}
   * @throws IllegalArgumentException when an empty string is passed in for the {@link Environment} name
   * or when the file cannot be parsed properly
   * @throws NotFoundException when a file corresponding to the requested environment is not found
   */
  @Override
  public Properties getConfiguration(Environment environment) {
    LOG.trace("Requesting configuration for environment: " + environment.getName());

    if (!initialized) {
      throw new IllegalStateException("Configuration source has to be successfully initialized before you request configuration.");
    }

    if(environment.getName().isEmpty()){
      throw new IllegalArgumentException("Environment must not be null or empty");
    }

    if (!s3wrapper.isFileExist(environment.getName())) {
      throw new NotFoundException(MessageFormat.format("File name {0} does not exist", environment.getName()));
    }

    S3Object fileFromS3 = s3wrapper.getFile(environment.getName());

    Properties properties = new Properties();
    try (InputStream input = fileFromS3.getObjectContent()) {
      properties.load(input);
    } catch (IOException e) {
      throw new IllegalArgumentException("Unable to load configuration from " + environment.getName() + " file", e);
    }
    finally {
      try {
        fileFromS3.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

    return properties;
  }

  /**
   * @throws ExceptionInInitializerError when the requested bucket does not exist
   */
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

