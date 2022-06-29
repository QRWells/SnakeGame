package wang.qrwells.net.server.bytemsg.allocator;

public interface PoolByteMsgAllocator extends ByteMsgAllocator {
  int chunkSize();
}
