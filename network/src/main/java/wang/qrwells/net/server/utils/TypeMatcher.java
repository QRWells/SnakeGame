package wang.qrwells.net.server.utils;

public class TypeMatcher {
  private final Class<?> type;

  public TypeMatcher(Class<?> type) {
    this.type = type;
  }

  public boolean match(Object msg) {
    return type.isInstance(msg);
  }
}
