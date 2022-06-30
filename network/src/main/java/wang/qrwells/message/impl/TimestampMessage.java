package wang.qrwells.message.impl;

import wang.qrwells.message.AbstractMessage;
import wang.qrwells.message.MessageType;

import java.nio.ByteBuffer;

public class TimestampMessage extends AbstractMessage {
  public long timestamp;

  public TimestampMessage(long timestamp) {
    this.timestamp = timestamp;
  }

  public TimestampMessage() {
    this.timestamp = System.currentTimeMillis();
  }

  @Override
  public int getLength() {
    return HEADER_LENGTH + Long.BYTES;
  }

  @Override
  public int getMessageType() {
    return MessageType.TIME;
  }

  @Override
  public byte[] getBytes() {
    var buf = ByteBuffer.allocate(getLength());
    writeHeader(buf);
    buf.putLong(timestamp);
    return buf.array();
  }
}
