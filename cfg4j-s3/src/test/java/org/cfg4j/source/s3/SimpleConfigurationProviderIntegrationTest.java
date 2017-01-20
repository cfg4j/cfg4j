package org.cfg4j.source.s3;

import com.amazonaws.services.s3.model.S3Object;
import org.cfg4j.provider.ConfigurationProvider;
import org.cfg4j.provider.ConfigurationProviderBuilder;
import org.cfg4j.source.ConfigurationSource;
import org.cfg4j.source.context.environment.Environment;
import org.cfg4j.source.context.environment.ImmutableEnvironment;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.ByteArrayInputStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class SimpleConfigurationProviderIntegrationTest {

  @Mock
  private AmazonS3Wrapper s3Wrapper;

  private final String BUCKET_NAME = "configurations-bucket";
  private final String FILE_NAME = "application.properties";
  private Environment environment = new ImmutableEnvironment(FILE_NAME);
  private String fileContent = "app.version=1.0.0";
  S3Object s3Object;

  @Before
  public void setUp() throws Exception {
     s3Object = new S3Object();
    s3Object.setKey(FILE_NAME);
    s3Object.setObjectContent(new ByteArrayInputStream(fileContent.getBytes()));

    when(s3Wrapper.getFile(FILE_NAME)).thenReturn(s3Object);
    when(s3Wrapper.isFileExist(FILE_NAME)).thenReturn(true);
    when(s3Wrapper.getBucketName()).thenReturn(BUCKET_NAME);
    when(s3Wrapper.isBucketExist(BUCKET_NAME)).thenReturn(true);
    when(s3Wrapper.getFileContent(s3Object)).thenReturn(fileContent);
  }

  @Test
  public void readsConfigsFromS3ConfigurationSource() throws Exception {
    ConfigurationSource source = new S3ConfigurationSourceBuilder().build(s3Wrapper);

    ConfigurationProvider provider = new ConfigurationProviderBuilder()
      .withConfigurationSource(source)
      .withEnvironment(environment)
      .build();

    assertThat(provider.getProperty("app.version", String.class)).isEqualTo("1.0.0");
  }
}
