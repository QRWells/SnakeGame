package wang.qrwells.net.server.pipeline;

import wang.qrwells.net.server.channel.Channel;
import wang.qrwells.net.server.handler.ChannelReadHandler;
import wang.qrwells.net.server.handler.ChannelWriteHandler;
import wang.qrwells.net.server.pipeline.Interface.HandlerNode;
import wang.qrwells.net.server.pipeline.Interface.MessageHandlerContext;

import static wang.qrwells.net.server.utils.EventLoopUtil.inEventLoop;
import static wang.qrwells.net.server.utils.EventLoopUtil.runOnContext;

public class DefaultMessageHandlerContext implements MessageHandlerContext {
  private final Channel channel;

  private final Pipeline pipeline;
  private final TailNode tail;
  private final HeadNode head;
  private HandlerNode now;

  public DefaultMessageHandlerContext(Channel channel, Pipeline pipeline, TailNode tail, HeadNode head) {
    this.channel = channel;
    this.pipeline = pipeline;
    this.tail = tail;
    this.head = head;
    now = head;
  }

  @Override
  public Channel channel() {
    return channel;
  }

  @Override
  public Pipeline pipeline() {
    return pipeline;
  }

  @Override
  public void nextRead(Object msg) throws Throwable {
    if (!inEventLoop(channel)) {
      runOnContext(channel, () -> {
        try {
          nextRead(msg);
        } catch (Throwable e) {
          pipeline.catchException(e);
        }
      });
      return;
    }
    if (!isRemoved()) {
      now = getNextReadNode();
      ((ChannelReadHandler) now.getHandler()).read(this, msg);
    }
  }

  @Override
  public void nextWrite(Object msg) throws Throwable {
    if (!inEventLoop(channel)) {
      runOnContext(channel, () -> {
        try {
          nextWrite(msg);
        } catch (Throwable throwable) {
          pipeline.catchException(throwable);
        }
      });
      return;
    }
    if (!isRemoved()) {
      now = getNextWriteNode();
      ((ChannelWriteHandler) now.getHandler()).write(this, msg);
    }
  }

  @Override
  public void nextExceptionHandler(Throwable t) {
    if (!inEventLoop(channel)) {
      runOnContext(channel, () -> nextExceptionHandler(t));
      return;
    }
    if (!isRemoved()) {
      now = getNextNode();
      now.getHandler().onException(this, t);
    }

  }

  @Override
  public void nextActive() throws Throwable {
    if (!inEventLoop(channel)) {
      runOnContext(channel, () -> {
        try {
          nextActive();
        } catch (Throwable throwable) {
          pipeline.catchException(throwable);
        }
      });
      return;
    }
    if (!isRemoved()) {
      now = getNextNode();
      now.getHandler().onActive(this);
    }
  }

  @Override
  public void nextInactive() throws Throwable {
    if (!inEventLoop(channel)) {
      runOnContext(channel, () -> {
        try {
          nextInactive();
        } catch (Throwable throwable) {
          pipeline.catchException(throwable);
        }
      });
      return;
    }
    if (!isRemoved()) {
      now = getNextNode();
      now.getHandler().onInactive(this);
    }
  }

  private boolean isRemoved() {
    HandlerNode it = head;
    while (it != now) {
      if (it == tail)
        return true;
      it = it.next();
    }
    return false;
  }


  private HandlerNode getNextReadNode() {
    var handlerNode = now.next();
    while (!handlerNode.isReadHandler())
      handlerNode = handlerNode.next();

    return handlerNode;
  }

  private HandlerNode getNextWriteNode() {
    var handlerNode = now.next();
    while (!handlerNode.isWriteHandler())
      handlerNode = handlerNode.next();
    return handlerNode;
  }

  private HandlerNode getNextNode() {
    return now.next();
  }
}
