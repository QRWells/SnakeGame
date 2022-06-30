package wang.qrwells.handler;

import org.slf4j.Logger;
import wang.qrwells.message.impl.LogoutMessage;
import wang.qrwells.net.server.ServerContext;
import wang.qrwells.net.server.handler.impl.SimpleChannelReadHandler;
import wang.qrwells.net.server.pipeline.Interface.MessageHandlerContext;

public class LogoutMessageHandler extends SimpleChannelReadHandler<LogoutMessage> {
  private final Logger logger = org.slf4j.LoggerFactory.getLogger(LogoutMessageHandler.class);

  public LogoutMessageHandler() {
    super(LogoutMessage.class);
  }

  @Override
  public void read0(MessageHandlerContext ctx, LogoutMessage msg) throws Throwable {
    logger.info("User Id {} logout", msg.getUserId());
    ServerContext.INSTANCE.getUsers().remove(msg.getUserId());
  }
}
