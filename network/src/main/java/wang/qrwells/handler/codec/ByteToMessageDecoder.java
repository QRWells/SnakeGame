package wang.qrwells.handler.codec;

import org.slf4j.Logger;
import wang.qrwells.message.MessageUtil;
import wang.qrwells.net.server.bytemsg.msg.impl.FixedPoolByteMsg;
import wang.qrwells.net.server.handler.ChannelReadHandler;
import wang.qrwells.net.server.pipeline.Interface.MessageHandlerContext;

import java.nio.ByteBuffer;

public class ByteToMessageDecoder implements ChannelReadHandler {
  private final Logger logger = org.slf4j.LoggerFactory.getLogger(getClass());

  @Override
  public void read(MessageHandlerContext mhc, Object msg) throws Throwable {
    FixedPoolByteMsg fixedPoolByteMsg = (FixedPoolByteMsg) msg;
    var len = fixedPoolByteMsg.readInt();
    logger.info("read: " + len);
    var buf = new byte[len];
    fixedPoolByteMsg.readBytes(buf, 0, len);
    // log bytes in the buf
    var m = MessageUtil.parseMessage(ByteBuffer.wrap(buf));
    System.out.println(m.getClass().getName());
    mhc.nextRead(m);
  }
}
