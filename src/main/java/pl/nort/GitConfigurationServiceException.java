package pl.nort;

public class GitConfigurationServiceException extends RuntimeException {

  public GitConfigurationServiceException(String msg, Exception e) {
    super(msg, e);
  }

  public GitConfigurationServiceException(String msg) {
    super(msg);
  }
}
