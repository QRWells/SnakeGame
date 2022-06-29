package wang.qrwells.message.impl;


import wang.qrwells.message.AbstractMessage;
import wang.qrwells.message.MessageType;

import java.nio.ByteBuffer;

public class JoinToGroupMessage extends AbstractMessage {
  private final int groupId;
  private final int userId;

  public JoinToGroupMessage(int groupId, int userId) {
    this.groupId = groupId;
    this.userId = userId;
  }

  public int getGroupId() {
    return groupId;
  }

  public int getUserId() {
    return userId;
  }

  @Override
  public int getLength() {
    return HEADER_LENGTH + Integer.BYTES * 2;
  }

  @Override
  public int getMessageType() {
    return MessageType.JOIN_TO_GROUP;
  }

  @Override
  public byte[] getBytes() {
    var buf = ByteBuffer.allocate(getLength());
    writeHeader(buf);
    buf.putInt(groupId);
    buf.putInt(userId);
    return buf.array();
  }
}
