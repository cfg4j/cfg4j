package org.cfg4j.source.s3;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.util.IOUtils;

import java.io.IOException;
import java.io.InputStream;

public class AmazonS3WrapperImpl extends AWSWrapper implements AmazonS3Wrapper {

  private final String accessKey;
  private final String secretKey;

  private String bucketName;
  private AmazonS3Client s3client;

  public AmazonS3WrapperImpl(String accessKey, String secretKey, String bucketName) {
    this.accessKey = accessKey;
    this.secretKey = secretKey;
    this.bucketName = bucketName;
  }

  @Override
  public void init(){
    initCredentials(accessKey, secretKey);
    s3client = new AmazonS3Client(credentials);
  }

  @Override
  public boolean isFileExist(String fileName) {
    return fileName != null &&
      !fileName.isEmpty() &&
      s3client.doesObjectExist(getBucketName(), fileName);
  }

  @Override
  public boolean isBucketExist(String bucketName){
    return s3client.doesBucketExist(bucketName);
  }

  @Override
  public S3Object getFile(String fileName) {
    return s3client.getObject(new GetObjectRequest(bucketName, fileName));
  }

  @Override
  public String getFileContent(String fileName) {
    S3Object fileData = getFile(fileName);
    return getFileContent(fileData);
  }

  @Override
  public String getFileContent(S3Object obj){
    InputStream is = obj.getObjectContent();
    String fileContent = new String();

    try {
      fileContent = IOUtils.toString(is);
      obj.close();
      is.close();
    } catch (IOException e) {
      e.printStackTrace();
    }

    return fileContent;
  }

  public String getBucketName() {
    return bucketName;
  }

  public void setBucketName(String bucketName) {
    this.bucketName = bucketName;
  }
}
