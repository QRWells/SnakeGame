package wang.qrwells.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import wang.qrwells.db.DBClient;
import wang.qrwells.message.impl.JoinToGroupMessage;
import wang.qrwells.message.impl.ResponseMessage;
import wang.qrwells.net.server.handler.impl.SimpleChannelReadHandler;
import wang.qrwells.net.server.pipeline.Interface.MessageHandlerContext;

public class JoinToGroupMessageHandler extends SimpleChannelReadHandler<JoinToGroupMessage> {
  private final Logger log = LoggerFactory.getLogger(JoinToGroupMessageHandler.class);

  public JoinToGroupMessageHandler() {
    super(JoinToGroupMessage.class);
  }

  @Override
  public void read0(MessageHandlerContext ctx, JoinToGroupMessage msg) throws Throwable {
    // check if group and user exist
    var group = DBClient.INSTANCE.getGroupById(msg.getGroupId());
    var user = DBClient.INSTANCE.getUserById(msg.getUserId());
    if (group == null) {
      ctx.channel().writeAndFlush(new ResponseMessage(ResponseMessage.Status.ERROR, "group not found"));
    } else if (user == null) {
      ctx.channel().writeAndFlush(new ResponseMessage(ResponseMessage.Status.ERROR, "user not found"));
    } else {
      if (!DBClient.INSTANCE.userInGroup(msg.getUserId(), msg.getGroupId())) {
        DBClient.INSTANCE.addUserToGroup(msg.getUserId(), msg.getGroupId());
      }
      ctx.channel().writeAndFlush(new ResponseMessage(ResponseMessage.Status.OK, group.getName()));
    }
  }
}
