package wang.qrwells.message.impl;

import wang.qrwells.message.AbstractMessage;
import wang.qrwells.message.MessageType;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

public class FileMessage extends AbstractMessage {
  private final int senderId;
  private final int receiverId;
  private final String fileName;
  private final byte[] fileData;

  public FileMessage(int senderId, int receiverId, String fileName, byte[] fileData) {
    this.senderId = senderId;
    this.receiverId = receiverId;
    this.fileName = fileName;
    this.fileData = fileData;
  }

  public String getFileName() {
    return fileName;
  }

  public void writeToFile(File file) throws IOException {
    Files.write(file.toPath(), fileData);
  }

  @Override
  public int getLength() {
    return HEADER_LENGTH + Integer.BYTES * 2 + fileName.getBytes(StandardCharsets.UTF_8).length + fileData.length;
  }

  @Override
  public int getMessageType() {
    return MessageType.FILE;
  }

  @Override
  public byte[] getBytes() {
    var buf = ByteBuffer.allocate(getLength());
    writeHeader(buf);
    buf.putInt(senderId).putInt(receiverId).putInt(fileName.getBytes(StandardCharsets.UTF_8).length).putInt(
        fileData.length).put(fileName.getBytes(StandardCharsets.UTF_8)).put(fileData);
    return buf.array();
  }
}
