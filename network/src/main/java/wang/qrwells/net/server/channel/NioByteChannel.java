package wang.qrwells.net.server.channel;


import wang.qrwells.net.server.bytemsg.allocator.ByteMsgAllocator;
import wang.qrwells.net.server.bytemsg.msg.ByteMsg;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SocketChannel;

public class NioByteChannel extends Channel {
  private final int interOp;

  public NioByteChannel(Channel parentChannel, NioEventLoop nioEventLoop,
                        SelectableChannel javaChannel, int interOp)
      throws IOException {
    super(parentChannel, nioEventLoop, javaChannel);
    this.interOp = interOp;
  }

  @Override
  protected Object read0() throws IOException {
    ByteMsgAllocator allocator = eventLoop.getByteMsgAllocator();
    ByteMsg byteMsg = allocator.allocate(256);
    int readCount = byteMsg.readFromChannel(this);
    if (readCount < 0) {
      this.close();
      return null;
    }
    if (readCount == 0) {
      byteMsg.release();
      return null;
    }
    return byteMsg;
  }


  @Override
  public SocketChannel javaChanel() {
    return ((SocketChannel) javaChannel);
  }

  public void connect() {
    if (this.eventLoop() != Thread.currentThread()) {
      this.eventLoop().submit(this::connect);
      return;
    }
    pipeline.active();
  }

  public void connectToRemote(SocketAddress socketAddress) throws IOException {
    boolean success = javaChanel().connect(socketAddress);
    if (!success) {
      register();
    }
  }

  @Override
  public void register() {
    if (this.eventLoop() != Thread.currentThread()) {
      this.eventLoop().submit(this::register);
      return;
    }
    eventLoop.registerInterest(this, interOp);
  }
}
