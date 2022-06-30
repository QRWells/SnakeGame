package wang.qrwells.net.server.pipeline;

import wang.qrwells.net.server.handler.ChannelHandler;
import wang.qrwells.net.server.handler.ChannelReadHandler;
import wang.qrwells.net.server.handler.ChannelWriteHandler;
import wang.qrwells.net.server.pipeline.Interface.HandlerNode;

public class PlainNode extends HandlerNode {
  private final boolean read;
  private final boolean write;

  public PlainNode(Pipeline pipeline, ChannelHandler channelHandler) {
    super(pipeline, channelHandler);
    read = channelHandler instanceof ChannelReadHandler;
    write = channelHandler instanceof ChannelWriteHandler;
  }

  @Override
  public boolean isReadHandler() {
    return read;
  }

  @Override
  public boolean isWriteHandler() {
    return write;
  }
}
