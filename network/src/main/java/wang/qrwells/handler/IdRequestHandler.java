package wang.qrwells.handler;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import wang.qrwells.message.impl.IdRequestMessage;
import wang.qrwells.message.impl.ResponseMessage;
import wang.qrwells.message.impl.TimestampMessage;
import wang.qrwells.net.server.handler.impl.SimpleChannelReadHandler;
import wang.qrwells.net.server.pipeline.Interface.MessageHandlerContext;
import wang.qrwells.snake.db.DBClient;

public class IdRequestHandler extends
    SimpleChannelReadHandler<IdRequestMessage> {

  private final Logger log = LoggerFactory.getLogger(IdRequestHandler.class);

  public IdRequestHandler() {
    super(IdRequestMessage.class);
  }

  @Override
  public void read0(MessageHandlerContext ctx, IdRequestMessage msg)
      throws Throwable {
    if (msg.isGroup()) {
      var group = DBClient.INSTANCE.getGroupById(msg.getId());
      if (group == null) {
        // create a new group
      } else {
        ctx.channel()
           .write(
               new ResponseMessage(ResponseMessage.Status.OK, group.getName()));
      }
    } else {
      var user = DBClient.INSTANCE.getUserById(msg.getId());
      if (user == null) {
        ctx.channel()
           .write(new ResponseMessage(ResponseMessage.Status.ERROR,
                                      "user not found"));
      } else {
        log.info("Found user: " + user.getName());
        ctx.channel()
           .write(
               new ResponseMessage(ResponseMessage.Status.OK, user.getName()));
      }
    }
    ctx.channel()
       .writeAndFlush(new TimestampMessage());
  }
}
