package org.cfg4j.source.s3;

import com.amazonaws.services.kms.model.NotFoundException;
import com.amazonaws.services.s3.model.S3Object;
import org.cfg4j.source.ConfigurationSource;
import org.cfg4j.source.context.environment.Environment;
import org.cfg4j.source.context.environment.ImmutableEnvironment;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.ByteArrayInputStream;
import java.util.Properties;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class S3ConfigurationSourceIntegrationTest {

  @Mock
  private AmazonS3Wrapper s3Wrapper;

  private final String FILE_NAME = "application.properties";
  private Environment environment = new ImmutableEnvironment(FILE_NAME);

  private ConfigurationSource s3Source;

  @Before
  public void setUp() throws Exception {
    S3Object s3Object = new S3Object();
    s3Object.setKey(FILE_NAME);
    String fileContent = "app.version=1.0.0";
    s3Object.setObjectContent(new ByteArrayInputStream(fileContent.getBytes()));

    when(s3Wrapper.getFile(FILE_NAME)).thenReturn(s3Object);
    when(s3Wrapper.isFileExist(FILE_NAME)).thenReturn(true);
    String BUCKET_NAME = "configurations-bucket";
    when(s3Wrapper.getBucketName()).thenReturn(BUCKET_NAME);
    when(s3Wrapper.isBucketExist(BUCKET_NAME)).thenReturn(true);

    s3Source = new S3ConfigurationSourceBuilder().build(s3Wrapper);
  }

  @Test
  public void getPropertyWithSuccess() throws Exception {
    s3Source.init();
    Properties properties = s3Source.getConfiguration(environment);
    assertThat(properties.getProperty("app.version")).isEqualTo("1.0.0");
  }

  @Test(expected = NotFoundException.class)
  public void getNoExistFile() throws Exception {
    s3Source.init();
    String fileName = "no-exist-file";
    s3Source.getConfiguration(new ImmutableEnvironment(fileName));
  }

  @Test(expected = IllegalStateException.class)
  public void getConfigurationThrowsBeforeInitCalled() throws Exception {
    s3Source.getConfiguration(environment);
  }
}
