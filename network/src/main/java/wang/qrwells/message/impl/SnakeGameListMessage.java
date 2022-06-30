package wang.qrwells.message.impl;

import wang.qrwells.message.AbstractMessage;
import wang.qrwells.message.MessageType;

import java.nio.ByteBuffer;
import java.util.List;

public class SnakeGameListMessage extends AbstractMessage {
  private final List<GameInfo> gameInfos;

  public SnakeGameListMessage(List<GameInfo> gameInfos) {
    this.gameInfos = gameInfos;
  }

  public List<GameInfo> getGameInfos() {
    return gameInfos;
  }

  @Override
  public int getLength() {
    return HEADER_LENGTH + Integer.BYTES + gameInfos.size() * GameInfo.SIZE;
  }

  @Override
  public int getMessageType() {
    return MessageType.SNAKE_GAME_LIST;
  }

  @Override
  public byte[] getBytes() {
    ByteBuffer buffer = ByteBuffer.allocate(getLength());
    writeHeader(buffer);
    buffer.putInt(gameInfos.size());
    for (GameInfo gameInfo : gameInfos) {
      buffer.putInt(gameInfo.id);
      buffer.putInt(gameInfo.currentPlayers);
      buffer.putInt(gameInfo.maxPlayers);
    }
    return buffer.array();
  }

  public record GameInfo(int id, int currentPlayers, int maxPlayers) {
    static final int SIZE = Integer.BYTES * 3;
  }
}
