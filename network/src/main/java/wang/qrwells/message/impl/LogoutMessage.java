package wang.qrwells.message.impl;

import wang.qrwells.message.AbstractMessage;
import wang.qrwells.message.MessageType;

import java.nio.ByteBuffer;

public class LogoutMessage extends AbstractMessage {
  private final int userId;

  public LogoutMessage(int userId) {
    this.userId = userId;
  }

  public int getUserId() {
    return userId;
  }

  @Override
  public int getLength() {
    return Integer.BYTES + HEADER_LENGTH;
  }

  @Override
  public int getMessageType() {
    return MessageType.LOGOUT;
  }

  @Override
  public byte[] getBytes() {
    var result = ByteBuffer.allocate(getLength());
    writeHeader(result);
    result.putInt(userId);
    return result.array();
  }
}
