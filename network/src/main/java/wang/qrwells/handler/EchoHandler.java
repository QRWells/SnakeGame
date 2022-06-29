package wang.qrwells.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import wang.qrwells.net.server.channel.Channel;
import wang.qrwells.net.server.handler.impl.SimpleChannelReadHandler;
import wang.qrwells.net.server.pipeline.Interface.MessageHandlerContext;

import java.util.HashSet;

/**
 * Every client will have an instance of the handler.
 * To implement functions like broadcast, you may use a static field.
 */
public class EchoHandler extends SimpleChannelReadHandler<String> {
  private static final Logger logger = LoggerFactory.getLogger(EchoHandler.class);

  /**
   * static field to keep all clients of the server.
   */
  private static final HashSet<Channel> channels = new HashSet<>();

  public EchoHandler() {
    super(String.class);
  }

  /**
   * Override this for custom process.
   * @param ctx the context of the connection.
   * @param msg then actual message received.
   */
  @Override
  public void read0(MessageHandlerContext ctx, String msg) throws Throwable {
    logger.info("EchoHandler.read" + msg.length());
    // broadcast to other clients.
    for (var c : channels) {
      if (c != ctx.channel())
        c.writeAndFlush(msg);
    }
  }

  /**
   * Called when a connection established.
   * @param mhc the context of the connection.
   */
  @Override
  public void onAdded(MessageHandlerContext mhc) {
    logger.info("New user added.");
    channels.add(mhc.channel());
  }

  /**
   * Called when a client disconnected.
   * @param mhc the context of the connection.
   */
  @Override
  public void onInactive(MessageHandlerContext mhc) {
    logger.info("A user left.");
    channels.remove(mhc.channel());
  }
}
