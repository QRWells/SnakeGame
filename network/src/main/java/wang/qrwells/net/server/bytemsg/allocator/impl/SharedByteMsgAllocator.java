package wang.qrwells.net.server.bytemsg.allocator.impl;


import wang.qrwells.net.server.bytemsg.allocator.ByteMsgAllocator;
import wang.qrwells.net.server.bytemsg.allocator.PoolByteMsgAllocator;
import wang.qrwells.net.server.bytemsg.msg.ByteMsg;
import wang.qrwells.net.server.bytemsg.msg.impl.FixedPoolByteMsg;

import java.nio.ByteBuffer;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class SharedByteMsgAllocator implements PoolByteMsgAllocator {
  public static final SharedByteMsgAllocator DEFAULT_INSTANT = new SharedByteMsgAllocator(4 * 1024, 128);
  private final int poolSize;
  private final int chunkSize;
  private final AtomicBoolean isInit;
  private final int maxPoolSize;
  private final AtomicInteger nowSize;
  private ByteBuffer rawSpan;
  private Queue<FixedPoolByteMsg> waitMsg;


  public SharedByteMsgAllocator(int poolSize, int chunkSize) {
    this(poolSize, chunkSize, poolSize);
  }

  public SharedByteMsgAllocator(int poolSize, int chunkSize, int maxPoolSize) {
    int i = poolSize / (4 * 1024);
    //4K align
    this.poolSize = (i <= 0 ? 1 : i) * (4 * 1024);
    if (poolSize % chunkSize != 0) {
      throw new IllegalArgumentException("chunk size must be divisible by 4K");
    }
    this.chunkSize = chunkSize;
    this.maxPoolSize = maxPoolSize;
    isInit = new AtomicBoolean(false);
    nowSize = new AtomicInteger(poolSize);
  }


  @Override
  public void release(ByteMsg byteMsg) {
    if (byteMsg instanceof FixedPoolByteMsg && byteMsg.allocator() == this) {
      waitMsg.offer((FixedPoolByteMsg) byteMsg);
    }
  }

  @Override
  public FixedPoolByteMsg allocate(int size) {
    if (!isInit.get()) {
      if (isInit.compareAndSet(false, true)) {
        waitMsg = new ConcurrentLinkedQueue<>();
        init();
      }
    }
    FixedPoolByteMsg poll = waitMsg.poll();
    if (poll == null) {
      poll = nextLevelAllocate();
    }
    return poll;
  }


  private FixedPoolByteMsg nextLevelAllocate() {
    int size = nowSize.get();
    while (size + chunkSize <= maxPoolSize) {
      if (nowSize.compareAndSet(size, size + chunkSize)) {
        return new FakePoolByteMsg(this, chunkSize);
      }
      size = nowSize.get();
    }
    return new FakePoolByteMsg(null, chunkSize);
  }

  private void init() {
    int posCount = 0;
    rawSpan = ByteBuffer.allocateDirect(poolSize);
    while ((posCount + 1) * chunkSize <= poolSize) {
      ByteBuffer node = rawSpan.position(posCount * chunkSize).limit(posCount * chunkSize + chunkSize).slice();
      waitMsg.offer(new FixedPoolByteMsg(node, this));
      posCount++;
    }
  }


  @Override
  public int chunkSize() {
    return chunkSize;
  }


  /**
   * 处于可能池化 没有池化的两重态
   *
   * @see SharedByteMsgAllocator#nextLevelAllocate()
   */
  private static class FakePoolByteMsg extends FixedPoolByteMsg {

    public FakePoolByteMsg(ByteMsgAllocator allocator, int size) {
      super(ByteBuffer.allocateDirect(size), allocator);
    }

    @Override
    public void release() {
      if (allocator != null) {
        super.release();
      } else {
        internByteBuff = null;
      }
    }
  }


}
