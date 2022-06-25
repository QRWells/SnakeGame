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

  private final int x;
  private final int y;

  public SnakeGameMessage(long timeStamp, int playerId, Action action,
                          Direction direction) {
    this.timeStamp = timeStamp;
    this.playerId = playerId;
    this.action = action;
    this.direction = direction;
    this.x = -1;
    this.y = -1;
  }

  public SnakeGameMessage(long timeStamp, int playerId, Action action, int x,
                          int y) {
    this.timeStamp = timeStamp;
    this.playerId = playerId;
    this.action = action;
    this.direction = Direction.NONE;
    this.x = x;
    this.y = y;
  }

  public static SnakeGameMessage makeMessage(int playerId, Action action,
                                             Direction direction) {
    return new SnakeGameMessage(System.currentTimeMillis(), playerId, action,
                                direction);
  }

  public static SnakeGameMessage makeMessage(int playerId, Action action, int x,
                                             int y) {
    return new SnakeGameMessage(System.currentTimeMillis(), playerId, action, x,
                                y);
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
    return HEADER_LENGTH + Long.BYTES + Integer.BYTES * 3;
  }

  @Override
  public int getMessageType() {
    return MessageType.GAME_SNAKE;
  }

  @Override
  public byte[] getBytes() {
    var result = ByteBuffer.allocate(getLength());
    writeHeader(result);
    result.putLong(timeStamp)
          .putInt(playerId)
          .putInt(action.getOrdinal())
          .putInt(direction.getOrdinal());
    return result.array();
  }

  public enum Action {
    MOVE(0), EAT(1), DIE(2), START(3), STOP(4);
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

    private static final Map<Integer, Direction> ordinalToDirection =
        new HashMap<>();

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
