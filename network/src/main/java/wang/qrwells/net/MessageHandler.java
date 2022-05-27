package wang.qrwells.net;

public interface MessageHandler {
  void onReceive(Connection connection, Message message);
}
