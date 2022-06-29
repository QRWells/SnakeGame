package wang.qrwells.net.server.handler;


import wang.qrwells.net.server.pipeline.Interface.MessageHandlerContext;

public interface ChannelWriteHandler extends ChannelHandler {
  default void write(MessageHandlerContext mhc, Object msg) throws Throwable {
    mhc.nextWrite(msg);
  }
}
