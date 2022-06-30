package wang.qrwells.net.server.channel;

import java.util.concurrent.atomic.AtomicInteger;

public class NioEventLoopGroup {
  private final NioEventLoop[] nioEventLoops;
  private final AtomicInteger choose;

  public NioEventLoopGroup(int size) {
    nioEventLoops = new NioEventLoop[size];
    for (int i = 0; i < size; i++)
      nioEventLoops[i] = new NioEventLoop();
    choose = new AtomicInteger();
  }

  public void start() {
    for (NioEventLoop loop : nioEventLoops)
      loop.start();
  }

  public void close() {
    for (NioEventLoop loop : nioEventLoops)
      loop.close();
  }

  public NioEventLoop getEventLoop() {
    return nioEventLoops[choose.getAndIncrement() % nioEventLoops.length];
  }
}
