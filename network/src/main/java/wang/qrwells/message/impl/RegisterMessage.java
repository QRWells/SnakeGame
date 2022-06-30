package wang.qrwells.message.impl;

import wang.qrwells.message.AbstractMessage;
import wang.qrwells.message.MessageType;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class RegisterMessage extends AbstractMessage {
  private final String username;
  private final String password;

  public RegisterMessage(String userName, String password) {
    this.username = userName;
    this.password = password;
  }

  public String getUsername() {
    return username;
  }

  public String getPassword() {
    return password;
  }

  @Override
  public int getLength() {
    return HEADER_LENGTH + Integer.BYTES * 2 + username.getBytes(StandardCharsets.UTF_8).length + password.getBytes(
        StandardCharsets.UTF_8).length;
  }

  @Override
  public int getMessageType() {
    return MessageType.REGISTER;
  }

  @Override
  public byte[] getBytes() {
    var result = ByteBuffer.allocate(getLength());
    writeHeader(result);
    result.putInt(username.length()).putInt(password.length()).put(username.getBytes(StandardCharsets.UTF_8)).put(
        password.getBytes(StandardCharsets.UTF_8));
    return result.array();
  }
}
