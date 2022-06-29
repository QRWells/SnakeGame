package wang.qrwells.net.server.handler;

import wang.qrwells.net.server.pipeline.Interface.MessageHandlerContext;

public interface ChannelReadHandler extends ChannelHandler {
  default void read(MessageHandlerContext mhc, Object msg) throws Throwable {
    mhc.nextRead(msg);
  }
}
