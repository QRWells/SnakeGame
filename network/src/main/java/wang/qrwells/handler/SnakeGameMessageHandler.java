package wang.qrwells.handler;

import kotlin.random.Random;
import org.slf4j.Logger;
import wang.qrwells.message.impl.SnakeGameListMessage;
import wang.qrwells.message.impl.SnakeGameMessage;
import wang.qrwells.net.server.channel.Channel;
import wang.qrwells.net.server.handler.impl.SimpleChannelReadHandler;
import wang.qrwells.net.server.pipeline.Interface.MessageHandlerContext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SnakeGameMessageHandler extends SimpleChannelReadHandler<SnakeGameMessage> {
  private static final HashMap<Integer, Session> sessions = new HashMap<>();

  private final Logger logger = org.slf4j.LoggerFactory.getLogger(SnakeGameMessageHandler.class);

  public SnakeGameMessageHandler() {
    super(SnakeGameMessage.class);
  }

  @Override
  public void read0(MessageHandlerContext ctx, SnakeGameMessage msg) throws Throwable {
    logger.info("received message with type: {}", msg.getAction().toString());
    switch (msg.getAction()) {
      case MOVE, EAT, DIE -> {
        var session = sessions.get(msg.getSessionId());
        if (session != null) {
          for (var player : session.players) {
            if (player.channel != ctx.channel()) {
              player.channel.writeAndFlush(msg);
            }
          }
        }
      }
      case NEW -> {
        // generate a new session id
        var sessionId = (int) (Math.random() * 1000000);
        // create a new session
        var session = new Session(msg.getX());
        // add the session to the sessions map
        sessions.put(sessionId, session);
      }
      case JOIN -> {
        // get the session id
        var sessionId = msg.getSessionId();
        // get the session
        var session = sessions.get(sessionId);
        // generate random x and y and direction
        var x = Random.Default.nextInt(20);
        var y = Random.Default.nextInt(20);
        var direction = SnakeGameMessage.Direction.fromOrdinal(Random.Default.nextInt(4));
        // create a new player
        var player = new Player(msg.getPlayerId(), ctx.channel(), x, y, direction);
        // send the player to the previous channel
        session.addPlayer(player);
        for (var p : session.players) {
          for (var p2 : session.players) {
            p.channel.writeAndFlush(
                new SnakeGameMessage(System.currentTimeMillis(), sessionId, p2.id, SnakeGameMessage.Action.JOIN,
                                     p2.direction, p2.x, p2.y));
          }
        }

        // add the player to the session
        // if the session is full, send the message to the channel
        // generate food position
        if (session.isFull()) {
          var foodX = Random.Default.nextInt(20);
          var foodY = Random.Default.nextInt(20);
          var message = new SnakeGameMessage(System.currentTimeMillis(), 0, 0, SnakeGameMessage.Action.NEW,
                                             SnakeGameMessage.Direction.NONE, foodX, foodY);
          for (var p : session.players) {
            p.channel.writeAndFlush(message);
          }
        }
      }
      case STOP -> {
        // get the session id
        var sessionId = msg.getSessionId();
        // get the session
        var session = sessions.get(sessionId);
        // remove the channel from the session
        if (session.players.isEmpty())
          sessions.remove(sessionId);
      }
      case QUERY -> {
        var gameInfos = new ArrayList<SnakeGameListMessage.GameInfo>();
        // iterate with key and value
        for (var entry : sessions.entrySet()) {
          if (entry.getValue().isFull())
            continue;
          var sessionId = entry.getKey();
          var session = entry.getValue();
          var gameInfo = new SnakeGameListMessage.GameInfo(sessionId, session.players.size(), session.maxPlayers);
          gameInfos.add(gameInfo);
        }
        var message = new SnakeGameListMessage(gameInfos);
        ctx.channel().writeAndFlush(message);
      }
    }
  }

  public static class Session {
    private final int maxPlayers;
    private final List<Player> players = new ArrayList<>();

    Session(int maxPlayers) {
      this.maxPlayers = maxPlayers;
    }

    public int getMaxPlayers() {
      return maxPlayers;
    }

    public void addPlayer(Player player) {
      players.add(player);
    }

    public void removePlayer(Player player) {
      players.remove(player);
    }

    public void clear() {
      players.clear();
    }

    public boolean isFull() {
      return players.size() >= maxPlayers;
    }
  }

  public record Player(int id, Channel channel, int x, int y, SnakeGameMessage.Direction direction) {
  }
}
