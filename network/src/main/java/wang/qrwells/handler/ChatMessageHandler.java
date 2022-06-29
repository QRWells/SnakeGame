package wang.qrwells.handler;

import wang.qrwells.message.impl.ChatMessage;
import wang.qrwells.net.server.channel.Channel;
import wang.qrwells.net.server.handler.impl.SimpleChannelReadHandler;
import wang.qrwells.net.server.pipeline.Interface.MessageHandlerContext;
import wang.qrwells.net.server.ServerContext;

public class ChatMessageHandler extends SimpleChannelReadHandler<ChatMessage> {
  public ChatMessageHandler() {
    super(ChatMessage.class);
  }

  @Override
  public void read0(MessageHandlerContext ctx, ChatMessage msg)
      throws Throwable {
    if (msg.isGroup()) {
      var tos = ServerContext.INSTANCE.getGroups()
                                      .get(msg.getReceiverId());
      for (Channel channel : tos) {
        if (channel != ctx.channel()) {
          channel.writeAndFlush(msg);
        }
      }
    } else {
      var to = ServerContext.INSTANCE.getUsers()
                                     .get(msg.getReceiverId());
      if (to != null) {
        to.writeAndFlush(msg);
      }
    }
  }
}
