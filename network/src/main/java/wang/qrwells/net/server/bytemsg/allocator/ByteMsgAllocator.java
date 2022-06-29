package wang.qrwells.net.server.bytemsg.allocator;


import wang.qrwells.net.server.bytemsg.msg.ByteMsg;

public interface ByteMsgAllocator {
  void release(ByteMsg byteMsg);

  ByteMsg allocate(int size);
}
