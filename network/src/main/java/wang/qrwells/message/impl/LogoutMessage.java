package wang.qrwells.message.impl;


import wang.qrwells.message.AbstractMessage;
import wang.qrwells.message.MessageType;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class LogoutMessage extends AbstractMessage {
  private final String username;

  public LogoutMessage(String userName) {
    this.username = userName;
  }

  public String getUsername() {
    return username;
  }

  @Override
  public int getLength() {
    return username.length() + HEADER_LENGTH;
  }

  @Override
  public int getMessageType() {
    return MessageType.LOGOUT;
  }

  @Override
  public byte[] getBytes() {
    var result = ByteBuffer.allocate(getLength());
    writeHeader(result);
    result.put(username.getBytes(StandardCharsets.UTF_8));
    return result.array();
  }
}
