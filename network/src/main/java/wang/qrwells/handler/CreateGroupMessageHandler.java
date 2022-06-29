package wang.qrwells.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import wang.qrwells.message.impl.CreateGroupMessage;
import wang.qrwells.message.impl.ResponseMessage;
import wang.qrwells.net.server.handler.impl.SimpleChannelReadHandler;
import wang.qrwells.net.server.pipeline.Interface.MessageHandlerContext;
import wang.qrwells.snake.db.DBClient;
public class CreateGroupMessageHandler extends
    SimpleChannelReadHandler<CreateGroupMessage> {
  private final Logger log = LoggerFactory.getLogger(
      CreateGroupMessageHandler.class);

  public CreateGroupMessageHandler() {
    super(CreateGroupMessage.class);
  }

  @Override
  public void read0(MessageHandlerContext ctx, CreateGroupMessage msg)
      throws Throwable {
    var group = DBClient.INSTANCE.getGroupByName(msg.getGroupName());
    if (group == null) {
      // create a new group
      var newGroup = DBClient.INSTANCE.addGroup(msg.getGroupName());
      if (newGroup == null) {
        ctx.channel()
           .writeAndFlush(new ResponseMessage(ResponseMessage.Status.ERROR,
                                              "Unable to create group due to " +
                                              "database error"));
      } else {
        ctx.channel()
           .writeAndFlush(new ResponseMessage(ResponseMessage.Status.OK,
                                              newGroup.getId()
                                                      .getValue()
                                                      .toString()));
      }
    } else {
      ctx.channel()
         .writeAndFlush(new ResponseMessage(ResponseMessage.Status.ERROR,
                                            "group with this name" +
                                            " already exists"));
    }
  }
}
