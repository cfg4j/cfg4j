package org.cfg4j.source.s3;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;

public abstract class AWSWrapper {
  protected static AWSCredentials credentials;

  protected void initCredentials(String accessKey, String secretKey) {
    if (credentials == null) {
      credentials = new BasicAWSCredentials(accessKey, secretKey);
    }
  }
}
