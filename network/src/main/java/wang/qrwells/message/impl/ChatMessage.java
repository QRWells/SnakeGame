package wang.qrwells.message.impl;

import wang.qrwells.message.AbstractMessage;
import wang.qrwells.message.MessageType;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

/**
 * contains
 * an int senderId,
 * an int receiverId whose MSB indicates
 * whether it is a group message
 * and a byte[] body
 */
public class ChatMessage extends AbstractMessage {
  private final String message;
  private final boolean isGroup;
  private final int senderId;
  private final int receiverId;

  public ChatMessage(int senderId, int targetId, boolean isGroup, String message) {
    this.message = message;
    this.isGroup = isGroup;
    this.senderId = senderId;
    this.receiverId = targetId;
  }

  public String getMessage() {
    return message;
  }

  public boolean isGroup() {
    return isGroup;
  }

  public int getSenderId() {
    return senderId;
  }

  public int getReceiverId() {
    return receiverId;
  }

  @Override
  public int getLength() {
    return HEADER_LENGTH + Integer.BYTES * 3 + (message != null ? message.getBytes(StandardCharsets.UTF_8).length : 0);
  }

  @Override
  public int getMessageType() {
    return MessageType.CHAT;
  }

  @Override
  public byte[] getBytes() {
    var bytes = message.getBytes(StandardCharsets.UTF_8);
    var result = ByteBuffer.allocate(getLength());
    writeHeader(result);
    result.putInt(senderId).putInt(receiverId | (isGroup ? (1 << 31) : 0)).putInt(bytes.length).put(bytes);
    return result.array();
  }
}
