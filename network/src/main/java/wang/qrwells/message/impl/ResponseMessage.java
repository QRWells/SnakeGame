package wang.qrwells.message.impl;


import wang.qrwells.message.AbstractMessage;
import wang.qrwells.message.MessageType;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class ResponseMessage extends AbstractMessage {
  private final String detail;
  private final int status;

  public ResponseMessage(int status) {
    this.status = status;
    this.detail = null;
  }

  public ResponseMessage(int status, String detail) {
    this.status = status;
    this.detail = detail;
  }

  public String getDetail() {
    return detail;
  }

  public int getStatus() {
    return status;
  }

  @Override
  public int getLength() {
    return HEADER_LENGTH + Integer.BYTES + (detail != null ? detail.getBytes(
        StandardCharsets.UTF_8).length : 0);
  }

  @Override
  public int getMessageType() {
    return MessageType.RESPONSE;
  }

  @Override
  public byte[] getBytes() {
    var buf = ByteBuffer.allocate(getLength());
    writeHeader(buf);
    buf.putInt(status);
    if (detail != null) {
      var bytes = detail.getBytes(StandardCharsets.UTF_8);
      buf.putInt(bytes.length);
      buf.put(bytes);
    } else {
      buf.putInt(0);
    }
    return buf.array();
  }

  public static class Status {
    public static final int OK = 0;
    public static final int ERROR = 1;
    public static final int CREATED = 2;
  }
}
