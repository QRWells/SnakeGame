package wang.qrwells.net.server.exception;

public class UnCaughtException extends RuntimeException {
  public UnCaughtException(Throwable e) {
    super(e);
  }
}
