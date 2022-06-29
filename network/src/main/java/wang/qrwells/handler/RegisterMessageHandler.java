package wang.qrwells.handler;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import wang.qrwells.message.impl.RegisterMessage;
import wang.qrwells.message.impl.ResponseMessage;
import wang.qrwells.message.impl.TimestampMessage;
import wang.qrwells.net.server.handler.impl.SimpleChannelReadHandler;
import wang.qrwells.net.server.pipeline.Interface.MessageHandlerContext;
import wang.qrwells.net.server.ServerContext;
import wang.qrwells.snake.db.DBClient;

public class RegisterMessageHandler extends
    SimpleChannelReadHandler<RegisterMessage> {
  public RegisterMessageHandler() {
    super(RegisterMessage.class);
  }

  @Override
  public void read0(MessageHandlerContext ctx, RegisterMessage msg)
      throws Throwable {
    var user = DBClient.INSTANCE.getUserByName(msg.getUsername());
    if (user != null) {
      ctx.channel()
         .writeAndFlush(new ResponseMessage(ResponseMessage.Status.ERROR,
                                            "user already exists"));
    } else {
      var pwd = new BCryptPasswordEncoder().encode(msg.getPassword());
      var u = DBClient.INSTANCE.addUser(msg.getUsername(), pwd);
      if (u != null) {
        ServerContext.INSTANCE.getUsers()
                              .put(u.getId()
                                    .getValue(), ctx.channel());
        ctx.channel()
           .writeAndFlush(new ResponseMessage(ResponseMessage.Status.CREATED,
                                              u.getId()
                                               .getValue()
                                               .toString()));
      } else {
        ctx.channel()
           .writeAndFlush(new ResponseMessage(ResponseMessage.Status.ERROR,
                                              "Unable to register due to " +
                                              "database error"));
      }
    }
    ctx.channel()
       .write(new TimestampMessage());
    ctx.channel()
       .flush();
  }
}
