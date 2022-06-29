package wang.qrwells.message.impl;

import wang.qrwells.message.AbstractMessage;
import wang.qrwells.message.MessageType;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class CreateGroupMessage extends AbstractMessage {
  private final String groupName;

  public CreateGroupMessage(String groupName) {
    this.groupName = groupName;
  }

  public String getGroupName() {
    return groupName;
  }

  @Override
  public int getLength() {
    return HEADER_LENGTH + Integer.BYTES + groupName.getBytes(
        StandardCharsets.UTF_8).length;
  }

  @Override
  public int getMessageType() {
    return MessageType.CREATE_GROUP;
  }

  @Override
  public byte[] getBytes() {
    var buf = ByteBuffer.allocate(getLength());
    writeHeader(buf);
    var bytes = groupName.getBytes(StandardCharsets.UTF_8);
    buf.putInt(bytes.length);
    buf.put(bytes);
    return buf.array();
  }
}
