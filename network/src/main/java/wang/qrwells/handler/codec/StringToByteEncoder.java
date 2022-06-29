package wang.qrwells.handler.codec;

import wang.qrwells.net.server.handler.ChannelWriteHandler;
import wang.qrwells.net.server.pipeline.Interface.MessageHandlerContext;

import java.nio.charset.StandardCharsets;

public class StringToByteEncoder implements ChannelWriteHandler {
  @Override
  public void write(MessageHandlerContext mhc, Object msg) throws Throwable {
    var str = ((String) msg).getBytes(StandardCharsets.UTF_8);
    var bm = mhc.channel()
                .eventLoop()
                .getByteMsgAllocator()
                .allocate(64);
    bm.writeInt(str.length);
    bm.writeBytes(str);
    mhc.nextWrite(bm);
  }
}
