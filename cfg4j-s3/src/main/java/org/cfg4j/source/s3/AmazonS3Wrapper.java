package org.cfg4j.source.s3;

import com.amazonaws.services.s3.model.S3Object;

public interface AmazonS3Wrapper {
  void init();
  boolean isFileExist(String fileName);
  boolean isBucketExist(String bucketName);
  S3Object getFile(String fileName);
  String getFileContent(String fileName);
  String getFileContent(S3Object obj);
  String getBucketName();
  void setBucketName(String bucketName);
}
