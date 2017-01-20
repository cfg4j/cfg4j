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
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import java.io.ByteArrayInputStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class SimpleConfigurationProviderIntegrationTest {

  @Mock
  private AmazonS3Wrapper s3Wrapper;

  private final String FILE_NAME = "application.properties";
  private Environment environment = new ImmutableEnvironment(FILE_NAME);

  @Before
  public void setUp() throws Exception {
    String BUCKET_NAME = "configurations-bucket";
    S3Object mockS3Object = null;
    final String fileContent = "app.version=1.0.0";

    when(s3Wrapper.getFile(FILE_NAME))
      .thenAnswer(new Answer<S3Object>() {
        public S3Object answer(InvocationOnMock invocation) throws Throwable {
          S3Object mockS3Object = new S3Object();
          mockS3Object.setKey(FILE_NAME);
          mockS3Object.setObjectContent(new ByteArrayInputStream(fileContent.getBytes()));
          return mockS3Object;
        }
      });
    when(s3Wrapper.isFileExist(FILE_NAME)).thenReturn(true);
    when(s3Wrapper.getBucketName()).thenReturn(BUCKET_NAME);
    when(s3Wrapper.isBucketExist(BUCKET_NAME)).thenReturn(true);
    when(s3Wrapper.getFileContent(mockS3Object)).thenReturn(fileContent);
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
