package wang.qrwells.message.impl;


import wang.qrwells.message.AbstractMessage;
import wang.qrwells.message.MessageType;

import java.nio.ByteBuffer;

public class SnakeGameMessage extends AbstractMessage {
  public static final int MOVE_UP = 0x00;
  public static final int MOVE_DOWN = 0x01;
  public static final int MOVE_LEFT = 0x02;
  public static final int MOVE_RIGHT = 0x03;

  public static final int START = 0x10;
  public static final int STOP = 0x11;
  public static final int PAUSE = 0x12;

  private final long timeStamp;
  private final int playerId;
  private final int action;

  public SnakeGameMessage(long timeStamp, int playerId, int action) {
    this.timeStamp = timeStamp;
    this.playerId = playerId;
    if (action > 3 || action < 0) {
      throw new IllegalArgumentException("Invalid move: " + action);
    }
    this.action = action;
  }

  public static SnakeGameMessage makeMessage(int playerId, int action) {
    return new SnakeGameMessage(System.currentTimeMillis(), playerId, action);
  }

  @Override
  public int getLength() {
    return HEADER_LENGTH + Integer.BYTES * 3;
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
          .putInt(action);
    return result.array();
  }
}
