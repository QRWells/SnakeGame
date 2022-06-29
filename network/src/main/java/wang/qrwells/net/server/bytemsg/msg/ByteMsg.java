package wang.qrwells.net.server.bytemsg.msg;


import wang.qrwells.net.server.bytemsg.allocator.ByteMsgAllocator;
import wang.qrwells.net.server.channel.Channel;

import java.io.IOException;

public interface ByteMsg {
  int writeToChannel(Channel channel) throws IOException;

  int readFromChannel(Channel channel) throws IOException;

  int size();

  default void release() {
    allocator().release(this);
  }

  int maxCapacity();

  byte[] readBytes(int length);

  byte readByte();

  char readChar();

  short readShort();

  int readInt();

  long readLong();

  double readDouble();

  float readFloat();

  ByteMsg writByte(byte b);

  ByteMsg writeChar(char c);

  ByteMsg writeShort(short s);

  ByteMsg writeInt(int i);

  ByteMsg writeLong(long l);

  ByteMsg writeDouble(double d);

  ByteMsg writeFloat(float f);

  ByteMsg writeBytes(byte[] bytes);

  ByteMsgAllocator allocator();
}
