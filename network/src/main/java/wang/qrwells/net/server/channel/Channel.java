package wang.qrwells.net.server.channel;


import wang.qrwells.net.server.bytemsg.msg.ByteMsg;
import wang.qrwells.net.server.pipeline.Pipeline;

import java.io.IOException;
import java.nio.channels.SelectableChannel;

public abstract class Channel {
  protected Pipeline pipeline;
  protected NioEventLoop eventLoop;
  protected Channel parentChannel;
  protected SelectableChannel javaChannel;
  protected volatile boolean close;

  public Channel(Channel parentChannel, NioEventLoop nioEventLoop,
                 SelectableChannel javaChannel) throws IOException {
    this.parentChannel = parentChannel;
    eventLoop = nioEventLoop;
    this.javaChannel = javaChannel;
    pipeline = new Pipeline(this);
    close = false;
  }

  public SelectableChannel javaChanel() {
    return javaChannel;
  }

  public void read() {
    if (Thread.currentThread() != eventLoop) {
      eventLoop.submit(this::read);
      return;
    }
    Object msg = null;
    try {
      msg = read0();
      while (msg != null) {
        pipeline.read(msg);
        msg = read0();
      }
    } catch (Throwable t) {
      if (msg instanceof ByteMsg) {
        ((ByteMsg) msg).release();
      }
      pipeline.catchException(t);
    }
  }

  protected abstract Object read0() throws Exception;

  public void write(Object msg) {
    if (Thread.currentThread() != eventLoop) {
      eventLoop.submit(() -> write(msg));
      return;
    }
    try {
      pipeline.write(msg);
    } catch (Throwable t) {
      pipeline.catchException(t);
    }
  }

  public void writeAndFlush(Object msg) {
    if (Thread.currentThread() != eventLoop) {
      eventLoop.submit(() -> writeAndFlush(msg));
      return;
    }
    try {
      pipeline.write(msg);
      pipeline.flush();
    } catch (Throwable t) {
      pipeline.catchException(t);
    }
  }

  public void flush() {
    if (Thread.currentThread() != eventLoop) {
      eventLoop.submit(this::flush);
      return;
    }
    try {
      pipeline.flush();
    } catch (IOException e) {
      pipeline.catchException(e);
    }
  }

  public void close() throws IOException {
    if (close) {
      return;
    }
    synchronized (this) {
      if (close) {
        return;
      }
      javaChannel.close();
      close = true;
    }
    pipeline.inactive();
  }

  public NioEventLoop eventLoop() {
    return eventLoop;
  }

  public Pipeline getPipeline() {
    return pipeline;
  }


  public abstract void register();

  public void inactive() {
    if (Thread.currentThread() != eventLoop) {
      eventLoop.submit(this::inactive);
      return;
    }
    try {
      pipeline.inactive();
    } catch (Throwable e) {
      pipeline.catchException(e);
    }
  }

  public Channel parentChannel() {
    return parentChannel;
  }

  public void catchException(Throwable t) {
    if (Thread.currentThread() != eventLoop) {
      eventLoop.submit(() -> catchException(t));
      return;
    }
    pipeline.catchException(t);
  }

}
