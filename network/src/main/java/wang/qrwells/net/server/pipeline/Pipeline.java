package wang.qrwells.net.server.pipeline;

import wang.qrwells.net.server.channel.Channel;
import wang.qrwells.net.server.channel.NioEventLoop;
import wang.qrwells.net.server.handler.ChannelHandler;
import wang.qrwells.net.server.handler.ChannelReadHandler;
import wang.qrwells.net.server.handler.ChannelWriteHandler;
import wang.qrwells.net.server.pipeline.Interface.HandlerNode;
import wang.qrwells.net.server.pipeline.Interface.MessageHandlerContext;

import java.io.IOException;

public class Pipeline extends NioEventLoop {
  private final Channel channel;
  private final TailNode tailNode;
  private final HeadNode headNode;

  public Pipeline(Channel channel) {
    this.channel = channel;
    tailNode = new TailNode(this);
    headNode = new HeadNode(this);
    tailNode.setPre(headNode);
    headNode.setNext(tailNode);
  }

  public void read(Object msg) throws Throwable {
    ((ChannelReadHandler) headNode.getHandler()).read(
        creatMessageHandlerContext(), msg);
  }

  public void active() {
    try {
      headNode.getHandler().onActive(creatMessageHandlerContext());
    } catch (Throwable throwable) {
      catchException(throwable);
    }
  }

  public void inactive() {
    try {
      headNode.getHandler().onInactive(creatMessageHandlerContext());
    } catch (Throwable throwable) {
      catchException(throwable);
    }
  }

  public void catchException(Throwable throwable) {
    headNode.getHandler().onException(creatMessageHandlerContext(), throwable);
  }

  public void write(Object msg) {
    try {
      ((ChannelWriteHandler) headNode.getHandler()).write(
          creatMessageHandlerContext(), msg);
    } catch (Throwable throwable) {
      catchException(throwable);
    }
  }

  public void flush() throws IOException {
    tailNode.flush();
  }

  public void removeHandler(ChannelHandler channelHandler) {
    if (channel.eventLoop() != Thread.currentThread()) {
      channel.eventLoop().submit(() -> removeHandler(channelHandler));
      return;
    }
    HandlerNode it = this.headNode;
    while (it.getHandler() != channelHandler) {
      if (it == tailNode) return;
      it = it.next();
    }
    it.pre().setNext(it.next());
    it.next().setPre(it.pre());
    it.setPre(null);
    it.setNext(null);
    it.getHandler().onRemove(creatMessageHandlerContext());
  }

  public void addLast(ChannelHandler channelHandler) {
    if (channel.eventLoop() != Thread.currentThread()) {
      channel.eventLoop().submit(() -> addLast(channelHandler));
      return;
    }
    PlainNode plainNode = new PlainNode(this, channelHandler);
    plainNode.setPre(tailNode.pre());
    tailNode.pre().setNext(plainNode);
    tailNode.setPre(plainNode);
    plainNode.setNext(tailNode);
    channelHandler.onAdded(creatMessageHandlerContext());
  }

  public Channel getChannel() {
    return channel;
  }

  private MessageHandlerContext creatMessageHandlerContext() {
    return new DefaultMessageHandlerContext(channel, this, tailNode, headNode);
  }
}
