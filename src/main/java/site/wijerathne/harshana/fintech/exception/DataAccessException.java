package site.wijerathne.harshana.fintech.exception;

public class DataAccessException extends RuntimeException {
  public DataAccessException(String message , Throwable cause) {
    super(message, cause);
  }
}
