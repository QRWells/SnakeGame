package wang.qrwells.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCrypt;
import wang.qrwells.db.DBClient;
import wang.qrwells.message.impl.LoginMessage;
import wang.qrwells.message.impl.ResponseMessage;
import wang.qrwells.net.server.ServerContext;
import wang.qrwells.net.server.handler.impl.SimpleChannelReadHandler;
import wang.qrwells.net.server.pipeline.Interface.MessageHandlerContext;

import java.util.HashSet;

public class LoginMessageHandler extends SimpleChannelReadHandler<LoginMessage> {
  private final Logger log = LoggerFactory.getLogger(LoginMessageHandler.class);

  public LoginMessageHandler() {
    super(LoginMessage.class);
  }

  @Override
  public void read0(MessageHandlerContext ctx, LoginMessage msg) throws Throwable {
    //database
    var user = DBClient.INSTANCE.getUserByName(msg.getUsername());
    var response = ResponseMessage.Status.OK;
    var responseDetail = "";
    if (user == null) {
      response = ResponseMessage.Status.ERROR;
      responseDetail = "user not found";
    } else if (!BCrypt.checkpw(msg.getPassword(), user.getPassword())) {
      response = ResponseMessage.Status.ERROR;
      responseDetail = "password incorrect";
    } else {
      responseDetail = user.getId().toString();
      ServerContext.INSTANCE.getUsers().put(user.getId().getValue(), ctx.channel());
      ServerContext.INSTANCE.getUserGroup().get(user.getId().getValue()).forEach(groupId -> {
        if (!ServerContext.INSTANCE.getGroups().containsKey(groupId)) {
          ServerContext.INSTANCE.getGroups().put(groupId, new HashSet<>());
        }
        ServerContext.INSTANCE.getGroups().get(groupId).add(ctx.channel());
      });
    }
    log.info("Login response: " + response + " " + responseDetail);
    ctx.channel().writeAndFlush(new ResponseMessage(response, responseDetail));
  }
}
