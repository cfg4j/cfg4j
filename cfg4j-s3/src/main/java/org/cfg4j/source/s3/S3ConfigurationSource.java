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
import org.cfg4j.source.context.filesprovider.ConfigFilesProvider;
import org.cfg4j.source.context.propertiesprovider.PropertiesProvider;
import org.cfg4j.source.context.propertiesprovider.PropertiesProviderSelector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.*;

import static com.amazonaws.util.StringUtils.join;
import static java.util.Objects.requireNonNull;

class S3ConfigurationSource implements ConfigurationSource, Closeable {

  private static final Logger LOG = LoggerFactory.getLogger(S3ConfigurationSource.class);

  private boolean initialized;

  private String accessKey;
  private String secretKey;
  private String bucketName;
  private String prefix;
  private final ConfigFilesProvider configFilesProvider;
  private final PropertiesProviderSelector propertiesProviderSelector;
  private AmazonS3 s3Client;

  S3ConfigurationSource(String accessKey, String secretKey, String bucketName, String prefix,
                        ConfigFilesProvider configFilesProvider,
                        PropertiesProviderSelector propertiesProviderSelector) {
    this.accessKey = requireNonNull(accessKey);
    this.secretKey = requireNonNull(secretKey);
    this.bucketName = requireNonNull(bucketName);
    this.prefix = requireNonNull(prefix);
    this.initialized = false;
    this.propertiesProviderSelector = requireNonNull(propertiesProviderSelector);
    this.configFilesProvider = requireNonNull(configFilesProvider);
  }

  @Override
  public Properties getConfiguration(Environment environment) {
    if (!initialized) {
      throw new IllegalStateException("Configuration source has to be successfully initialized before you request configuration.");
    }

    Properties properties = new Properties();

    for (Path path : configFilesProvider.getConfigFiles()) {
        PropertiesProvider provider = propertiesProviderSelector.getProvider(path.getFileName().toString());
        properties.putAll(getProperties(environment, path.getFileName().toString(), provider));
    }

    return properties;

  }

  private Properties getProperties(Environment environment, String fileName, PropertiesProvider propertiesProvider) {
    String key = join("/", prefix, environment.getName(), fileName);
    GetObjectRequest s3request = new GetObjectRequest(this.bucketName, key);
    S3ObjectInputStream is = null;
    try {
      S3Object result = s3Client.getObject(s3request);
      is = result.getObjectContent();
      return propertiesProvider.getProperties(is);
    }
    catch (Exception e) {
      throw new IllegalStateException("Unable to load configuration from " + fileName + " file", e);
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

  @Override
  public void init() {
    LOG.info("Initializing " + S3ConfigurationSource.class + " pointing to " + bucketName + prefix);

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
      ", prefix='" + prefix + '\'' +
      '}';
  }
}
