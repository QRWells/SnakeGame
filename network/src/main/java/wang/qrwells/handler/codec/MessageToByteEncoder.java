package wang.qrwells.handler.codec;

import wang.qrwells.message.AbstractMessage;
import wang.qrwells.net.server.handler.ChannelWriteHandler;
import wang.qrwells.net.server.pipeline.Interface.MessageHandlerContext;

public class MessageToByteEncoder implements ChannelWriteHandler {

  @Override
  public void write(MessageHandlerContext mhc, Object msg) throws Throwable {
    var m = (AbstractMessage) msg;
    var bm = mhc.channel().eventLoop().getByteMsgAllocator().allocate(m.getLength());
    bm.writeInt(m.getLength());
    bm.writeBytes(m.getBytes());
    mhc.nextWrite(bm);
  }
}
