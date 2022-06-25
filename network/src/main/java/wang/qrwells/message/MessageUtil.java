package wang.qrwells.message;

import wang.qrwells.message.impl.*;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class MessageUtil {
  /**
   * Parse the message from the byte array. Assume the byte array contains
   * a full message.
   *
   * @param buffer the byte array to parse
   * @return the parsed message
   */
  public static AbstractMessage parseMessage(ByteBuffer buffer) {
    int type = buffer.getInt();
    return switch (type) {
      case MessageType.RESPONSE -> parseResponseMessage(buffer);
      case MessageType.REGISTER -> parseRegisterMessage(buffer);
      case MessageType.LOGIN -> parseLoginMessage(buffer);
      case MessageType.LOGOUT -> parseLogoutMessage(buffer);
      case MessageType.GAME_SNAKE -> parseSnakeGameMessage(buffer);
      case MessageType.CHAT -> parseChatMessage(buffer);
      case MessageType.TIME -> parseTimeMessage(buffer);
      case MessageType.FILE -> parseFileMessage(buffer);
      default -> throw new IllegalStateException("Unexpected value: " + type);
    };
  }

  private static FileMessage parseFileMessage(ByteBuffer buffer) {
    int senderId = buffer.getInt();
    int receiverId = buffer.getInt();
    int fileNameSize = buffer.getInt();
    int fileSize = buffer.getInt();
    byte[] fileName = new byte[fileNameSize];
    byte[] fileData = new byte[fileSize];
    buffer.get(fileName);
    buffer.get(fileData);

    return new FileMessage(senderId, receiverId,
                           new String(fileName, StandardCharsets.UTF_8),
                           fileData);
  }

  private static TimestampMessage parseTimeMessage(ByteBuffer buffer) {
    long time = buffer.getLong();
    return new TimestampMessage(time);
  }

  private static ResponseMessage parseResponseMessage(ByteBuffer buffer) {
    int status = buffer.getInt();
    int messageSize = buffer.getInt();
    if (messageSize == 0) {
      return new ResponseMessage(status);
    } else {
      byte[] message = new byte[messageSize];
      buffer.get(message);
      return new ResponseMessage(status,
                                 new String(message, StandardCharsets.UTF_8));
    }
  }

  public static ChatMessage parseChatMessage(ByteBuffer buffer) {
    var senderId = buffer.getInt();
    var receiverId = buffer.getInt();
    var isGroup = receiverId < 0;
    receiverId &= 0x7FFFFFFF;
    var msgLen = buffer.getInt();
    var msg = new byte[msgLen];
    buffer.get(msg, 0, msgLen);
    return new ChatMessage(senderId, receiverId, isGroup,
                           new String(msg, StandardCharsets.UTF_8));
  }

  public static RegisterMessage parseRegisterMessage(ByteBuffer buffer) {
    var nameLen = buffer.getInt();
    var name = new byte[nameLen];
    buffer.get(name, 0, nameLen);
    var pwdLen = buffer.getInt();
    var pwd = new byte[pwdLen];
    buffer.get(pwd, 0, pwdLen);
    return new RegisterMessage(new String(name, StandardCharsets.UTF_8),
                               new String(pwd, StandardCharsets.UTF_8));
  }

  public static LoginMessage parseLoginMessage(ByteBuffer buffer) {
    var nameLen = buffer.getInt();
    var name = new byte[nameLen];
    buffer.get(name, 0, nameLen);
    var pwdLen = buffer.getInt();
    var pwd = new byte[pwdLen];
    buffer.get(pwd, 0, pwdLen);
    return new LoginMessage(new String(name, StandardCharsets.UTF_8),
                            new String(pwd, StandardCharsets.UTF_8));
  }

  public static LogoutMessage parseLogoutMessage(ByteBuffer buffer) {
    var nameLen = buffer.getInt();
    var name = new byte[nameLen];
    buffer.get(name, 0, nameLen);
    return new LogoutMessage(new String(name, StandardCharsets.UTF_8));
  }

  public static SnakeGameMessage parseSnakeGameMessage(ByteBuffer buffer) {
    var time = buffer.getLong();
    var playerId = buffer.getInt();
    var action = SnakeGameMessage.Action.fromOrdinal(buffer.getInt());
    if (action == SnakeGameMessage.Action.EAT)
      return new SnakeGameMessage(time, playerId, action, buffer.getShort(),
                                  buffer.getShort());

    return new SnakeGameMessage(time, playerId, action,
                                SnakeGameMessage.Direction.fromOrdinal(
                                    buffer.getInt()));
  }
}
