package wang.qrwells.net.server.bytemsg.msg;

import wang.qrwells.net.server.exception.ByteMsgOverflowBound;

import java.nio.ByteBuffer;

public abstract class AbstractByteMsg implements ByteMsg {
  protected int readIndex;
  protected int writeIndex;
  protected int maxCapacity;
  protected ByteBuffer internByteBuff;

  public AbstractByteMsg(int maxCapacity) {
    this.maxCapacity = maxCapacity;
    readIndex = writeIndex = 0;
  }

  public AbstractByteMsg(ByteBuffer byteBuffer) {
    this(byteBuffer.capacity());
    internByteBuff = byteBuffer;
  }

  @Override
  public int size() {
    return writeIndex;
  }

  @Override
  public int maxCapacity() {
    return maxCapacity;
  }

  @Override
  public byte readByte() {
    if (readCheckBound(1)) {
      byte res = internByteBuff.get(readIndex);
      readIndex += 2;
      return res;
    }
    throw new ByteMsgOverflowBound();
  }

  @Override
  public byte[] readBytes(int length) {
    if (readCheckBound(length)) {
      byte[] bytes = new byte[length];
      int old = internByteBuff.position();
      internByteBuff.flip()
                    .position(readIndex)
                    .get(bytes)
                    .compact()
                    .position(old);
      readIndex += length;
      return bytes;
    }
    throw new ByteMsgOverflowBound();
  }

  @Override
  public char readChar() {
    if (readCheckBound(2)) {
      char res = internByteBuff.getChar(readIndex);
      readIndex += 2;
      return res;
    }
    throw new ByteMsgOverflowBound();
  }

  @Override
  public short readShort() {
    if (!readCheckBound(2)) {
      throw new ByteMsgOverflowBound();
    }
    short res = internByteBuff.getShort(readIndex);
    readIndex += 2;
    return res;
  }

  @Override
  public int readInt() {
    if (!readCheckBound(4)) {
      throw new ByteMsgOverflowBound();
    }
    int res = internByteBuff.getInt(readIndex);
    readIndex += 4;
    return res;
  }

  @Override
  public long readLong() {
    if (!readCheckBound(8)) {
      throw new ByteMsgOverflowBound();
    }
    long res = internByteBuff.getLong(readIndex);
    readIndex += 8;
    return res;
  }


  @Override
  public double readDouble() {
    if (!readCheckBound(8)) {
      throw new ByteMsgOverflowBound();
    }
    double res = internByteBuff.getDouble(readIndex);
    readIndex += 8;
    return res;
  }

  @Override
  public float readFloat() {
    if (!readCheckBound(4)) {
      throw new ByteMsgOverflowBound();
    }
    float res = internByteBuff.getFloat(readIndex);
    readIndex += 4;
    return res;
  }

  // 0 1 2 3
  private boolean readCheckBound(int length) {
    return readIndex + length <= writeIndex;
  }
}
