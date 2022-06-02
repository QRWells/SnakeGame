package wang.qrwells.net;

import wang.qrwells.message.AbstractMessage;

public interface MessageHandler<T extends AbstractMessage> {
  void onReceive(Connection connection, T message);
}
