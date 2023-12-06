package org.lks.junitwithdata.common.exception;

public class ResourceResolveException extends RuntimeException {

  public ResourceResolveException(String message) {
    super(message);
  }

  public ResourceResolveException(Throwable cause) {
    super(cause);
  }
}
