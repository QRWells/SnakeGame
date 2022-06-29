package wang.qrwells.net.server.pipeline.Interface;


import wang.qrwells.net.server.handler.ChannelHandler;
import wang.qrwells.net.server.handler.ChannelReadHandler;
import wang.qrwells.net.server.handler.ChannelWriteHandler;
import wang.qrwells.net.server.pipeline.Pipeline;

public abstract class HandlerNode {
  protected Pipeline pipeline;
  protected HandlerNode pre;
  protected HandlerNode next;
  protected ChannelHandler channelHandler;

  public HandlerNode(Pipeline pipeline, ChannelHandler channelHandler) {
    this.pipeline = pipeline;
    this.channelHandler = channelHandler;
  }

  public Pipeline getPipeline() {
    return pipeline;
  }

  public HandlerNode next() {
    return next;
  }

  public ChannelHandler getHandler() {
    return channelHandler;
  }

  public HandlerNode pre() {
    return pre;
  }

  public void setPre(HandlerNode handlerNode) {
    pre = handlerNode;
  }

  public void setNext(HandlerNode handlerNode) {
    next = handlerNode;
  }

  public boolean isReadHandler() {
    return getHandler() instanceof ChannelReadHandler;
  }

  public boolean isWriteHandler() {
    return getHandler() instanceof ChannelWriteHandler;
  }
}
