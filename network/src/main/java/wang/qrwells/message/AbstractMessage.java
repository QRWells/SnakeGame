package wang.qrwells.message;

import java.nio.ByteBuffer;

public abstract class AbstractMessage {
  public static final int HEADER_LENGTH = Integer.BYTES;

  public abstract int getLength();

  public abstract int getMessageType();

  public abstract byte[] getBytes();

  protected void writeHeader(ByteBuffer buffer) {
    assert buffer.capacity() >= HEADER_LENGTH;
    buffer.putInt(getMessageType());
  }
}
