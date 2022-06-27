package wang.qrwells.message.impl;


import wang.qrwells.message.AbstractMessage;
import wang.qrwells.message.MessageType;

import java.nio.ByteBuffer;

public class IdRequestMessage extends AbstractMessage {
  boolean isGroup;
  int id;

  public IdRequestMessage(boolean isGroup, int id) {
    this.isGroup = isGroup;
    this.id = id;
  }

  public boolean isGroup() {
    return isGroup;
  }

  public int getId() {
    return id;
  }

  @Override
  public int getLength() {
    return HEADER_LENGTH + Integer.BYTES;
  }

  @Override
  public int getMessageType() {
    return MessageType.ID_REQUEST;
  }

  @Override
  public byte[] getBytes() {
    var buf = ByteBuffer.allocate(getLength());
    writeHeader(buf);
    buf.putInt(id | (isGroup ? (1 << 31) : 0));
    return buf.array();
  }
}
