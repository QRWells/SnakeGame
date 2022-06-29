package wang.qrwells.handler;


import wang.qrwells.message.impl.LogoutMessage;
import wang.qrwells.net.server.ServerContext;
import wang.qrwells.net.server.handler.impl.SimpleChannelReadHandler;
import wang.qrwells.net.server.pipeline.Interface.MessageHandlerContext;

public class LogoutMessageHandler extends
    SimpleChannelReadHandler<LogoutMessage> {
  public LogoutMessageHandler() {
    super(LogoutMessage.class);
  }

  @Override
  public void read0(MessageHandlerContext ctx, LogoutMessage msg)
      throws Throwable {
    ServerContext.INSTANCE.getUsers()
                          .remove(msg.getUserId());
  }
}
