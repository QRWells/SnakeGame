package wang.qrwells.message.impl;

import wang.qrwells.message.AbstractMessage;
import wang.qrwells.message.MessageType;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

public class SnakeGameMessage extends AbstractMessage {
  private final Action action;
  private final long timeStamp;
  private final int playerId;
  private final Direction direction;
  private final int sessionId;
  private final int x;
  private final int y;

  public SnakeGameMessage(long timeStamp, int sessionId, int playerId, Action action, Direction direction, int x,
                          int y) {
    this.timeStamp = timeStamp;
    this.sessionId = sessionId;
    this.playerId = playerId;
    this.action = action;
    this.direction = direction;
    this.x = x;
    this.y = y;
  }

  public static SnakeGameMessage makeEatMessage(int sessionId, int id, int x, int y) {
    return new SnakeGameMessage(System.currentTimeMillis(), sessionId, id, Action.EAT, Direction.NONE, x, y);
  }


  public static SnakeGameMessage makeMoveMessage(int sessionId, int id, Direction direction) {
    return new SnakeGameMessage(System.currentTimeMillis(), sessionId, id, Action.MOVE, direction, 0, 0);
  }

  public int getX() {
    return x;
  }

  public int getY() {
    return y;
  }

  public Action getAction() {
    return action;
  }

  public Direction getDirection() {
    return direction;
  }

  public long getTimeStamp() {
    return timeStamp;
  }

  public int getPlayerId() {
    return playerId;
  }

  @Override
  public int getLength() {
    return HEADER_LENGTH + Long.BYTES + Integer.BYTES * 6;
  }

  @Override
  public int getMessageType() {
    return MessageType.GAME_SNAKE;
  }

  @Override
  public byte[] getBytes() {
    var result = ByteBuffer.allocate(getLength());
    writeHeader(result);
    result.putLong(timeStamp);
    result.putInt(sessionId);
    result.putInt(playerId);
    result.putInt(action.getOrdinal());
    result.putInt(direction.getOrdinal());
    result.putInt(x);
    result.putInt(y);
    return result.array();
  }

  public int getSessionId() {
    return sessionId;
  }

  public enum Action {
    MOVE(0), EAT(1), DIE(2), NEW(3), JOIN(4), STOP(5), QUERY(6);
    private static final Map<Integer, Action> ordinalToAction = new HashMap<>();

    static {
      for (Action action : Action.values()) {
        ordinalToAction.put(action.ordinal, action);
      }
    }

    private final int ordinal;

    Action(int ordinal) {
      this.ordinal = ordinal;
    }

    public static Action fromOrdinal(int ordinal) {
      return ordinalToAction.get(ordinal);
    }

    public int getOrdinal() {
      return ordinal;
    }
  }

  public enum Direction {
    NONE(-1), UP(0), DOWN(1), LEFT(2), RIGHT(3);

    private static final Map<Integer, Direction> ordinalToDirection = new HashMap<>();

    static {
      for (Direction direction : Direction.values()) {
        ordinalToDirection.put(direction.ordinal, direction);
      }
    }

    private final int ordinal;

    Direction(int ordinal) {
      this.ordinal = ordinal;
    }

    public static Direction fromOrdinal(int ordinal) {
      return ordinalToDirection.get(ordinal);
    }

    public int getOrdinal() {
      return ordinal;
    }
  }
}
