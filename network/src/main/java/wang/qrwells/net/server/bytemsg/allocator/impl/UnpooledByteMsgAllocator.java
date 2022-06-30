package wang.qrwells.net.server.bytemsg.allocator.impl;


import wang.qrwells.net.server.bytemsg.allocator.ByteMsgAllocator;
import wang.qrwells.net.server.bytemsg.msg.ByteMsg;
import wang.qrwells.net.server.bytemsg.msg.impl.UnpoolByteMsg;

import java.nio.ByteBuffer;

public class UnpooledByteMsgAllocator implements ByteMsgAllocator {
  private static final UnpooledByteMsgAllocator unpooledByteMsgAllocator = new UnpooledByteMsgAllocator();

  public static UnpooledByteMsgAllocator get() {
    return unpooledByteMsgAllocator;
  }

  @Override
  public void release(ByteMsg byteMsg) {
    // do nothing
  }

  @Override
  public UnpoolByteMsg allocate(int size) {
    return new UnpoolByteMsg(ByteBuffer.allocateDirect(size));
  }
}
