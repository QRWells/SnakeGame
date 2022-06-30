package wang.qrwells.net.server.handler.impl;

import wang.qrwells.net.server.bytemsg.msg.ByteMsg;
import wang.qrwells.net.server.handler.ChannelWriteHandler;
import wang.qrwells.net.server.pipeline.Interface.MessageHandlerContext;

public abstract class SimpleChannelWriteHandler<I> implements ChannelWriteHandler {
  public abstract void write0(MessageHandlerContext messageHandlerContext, I msg, ByteMsg byteMsg);

  @Override
  public void write(MessageHandlerContext mhc, Object msg) throws Throwable {
    ByteMsg byteMsg = mhc.channel().eventLoop().getByteMsgAllocator().allocate(128);
    write0(mhc, (I) msg, byteMsg);
  }
}
