package wang.qrwells.net.server.pipeline;

import wang.qrwells.net.server.handler.ChannelHandler;
import wang.qrwells.net.server.handler.ChannelReadHandler;
import wang.qrwells.net.server.handler.ChannelWriteHandler;
import wang.qrwells.net.server.pipeline.Interface.HandlerNode;

public class HeadNode extends HandlerNode {
  public HeadNode(Pipeline pipeline) {
    super(pipeline, new HeadNodeHandler());
  }

  @Override
  public Pipeline getPipeline() {
    return pipeline;
  }

  @Override
  public HandlerNode next() {
    return next;
  }

  @Override
  public HandlerNode pre() {
    return null;
  }

  @Override
  public void setPre(HandlerNode handlerNode) {
  }

  @Override
  public void setNext(HandlerNode handlerNode) {
    next = handlerNode;
  }

  @Override
  public ChannelHandler getHandler() {
    return channelHandler;
  }

  @Override
  public boolean isReadHandler() {
    return true;
  }

  @Override
  public boolean isWriteHandler() {
    return true;
  }

  private static class HeadNodeHandler implements ChannelReadHandler, ChannelWriteHandler {
  }
}
