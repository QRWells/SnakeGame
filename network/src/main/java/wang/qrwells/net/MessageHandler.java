package wang.qrwells.net;

import wang.qrwells.message.AbstractMessage;

public interface MessageHandler {
  void onReceive(Connection connection, AbstractMessage message);
}
