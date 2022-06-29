package wang.qrwells.handler.codec;


import wang.qrwells.net.server.bytemsg.msg.impl.FixedPoolByteMsg;
import wang.qrwells.net.server.handler.ChannelReadHandler;
import wang.qrwells.net.server.pipeline.Interface.MessageHandlerContext;

import java.nio.charset.StandardCharsets;

public class ByteToStringDecoder implements ChannelReadHandler {
  @Override
  public void read(MessageHandlerContext mhc, Object msg) throws Throwable {
    FixedPoolByteMsg fixedPoolByteMsg = (FixedPoolByteMsg) msg;
    var len = fixedPoolByteMsg.readInt();
    var buf = new byte[len];
    fixedPoolByteMsg.readBytes(buf, 0, len);
    mhc.nextRead(new String(buf, StandardCharsets.UTF_8));
  }
}
