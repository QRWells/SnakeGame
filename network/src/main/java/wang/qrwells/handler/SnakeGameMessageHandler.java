package wang.qrwells.handler;

import wang.qrwells.message.impl.SnakeGameListMessage;
import wang.qrwells.message.impl.SnakeGameMessage;
import wang.qrwells.net.server.channel.Channel;
import wang.qrwells.net.server.handler.impl.SimpleChannelReadHandler;
import wang.qrwells.net.server.pipeline.Interface.MessageHandlerContext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SnakeGameMessageHandler extends
    SimpleChannelReadHandler<SnakeGameMessage> {
  private static final HashMap<Integer, Session> sessions = new HashMap<>();

  public SnakeGameMessageHandler() {
    super(SnakeGameMessage.class);
  }

  @Override
  public void read0(MessageHandlerContext ctx, SnakeGameMessage msg)
      throws Throwable {
    switch (msg.getAction()) {
      case MOVE, EAT, DIE -> {
        var session = sessions.get(msg.getSessionId());
        if (session != null) {
          for (Channel channel : session.channels) {
            if (channel != ctx.channel()) {
              channel.writeAndFlush(msg);
            }
          }
        }
      }
      case START -> {
        // generate a new session id
        var sessionId = (int) (Math.random() * 1000000);
        // create a new session
        var session = new Session(msg.getX());
        // add the channel to the session
        session.addChannel(ctx.channel());
        // add the session to the sessions map
        sessions.put(sessionId, session);
      }
      case JOIN -> {
        // get the session id
        var sessionId = msg.getSessionId();
        // get the session
        var session = sessions.get(sessionId);
        // send join message to other channels in the session
        for (Channel channel : session.channels) {
          channel.writeAndFlush(msg);
        }
        // add the channel to the session
        session.addChannel(ctx.channel());
        if (session.maxPlayers == session.channels.size()) {
          // send start message to all channels in the session
          var startMessage = new SnakeGameMessage(msg.getTimeStamp(), sessionId,
                                                  0,
                                                  SnakeGameMessage.Action.START,
                                                  SnakeGameMessage.Direction.NONE,
                                                  -1, -1);
          for (Channel channel : session.channels) {
            channel.writeAndFlush(startMessage);
          }
        }
      }
      case STOP -> {
        // get the session id
        var sessionId = msg.getSessionId();
        // get the session
        var session = sessions.get(sessionId);
        // remove the channel from the session
        if (session.channels.isEmpty())
          sessions.remove(sessionId);
        else
          session.channels.remove(ctx.channel());
      }
      case QUERY -> {
        var gameInfos = new ArrayList<SnakeGameListMessage.GameInfo>();
        for (var i = 0; i < sessions.size(); i++) {
          var session = sessions.get(i);
          gameInfos.add(new SnakeGameListMessage.GameInfo(i, session.maxPlayers,
                                                          session.channels.size()));
        }
        var message = new SnakeGameListMessage(gameInfos);
        ctx.channel()
           .writeAndFlush(message);
      }
    }
  }

  public static class Session {
    private final int maxPlayers;
    private final List<Channel> channels = new ArrayList<>();

    Session(int maxPlayers) {
      this.maxPlayers = maxPlayers;
    }

    public int getMaxPlayers() {
      return maxPlayers;
    }

    public void addChannel(Channel channel) {
      channels.add(channel);
    }

    public void removeChannel(Channel channel) {
      channels.remove(channel);
    }

    public void clear() {
      channels.clear();
    }
  }
}
