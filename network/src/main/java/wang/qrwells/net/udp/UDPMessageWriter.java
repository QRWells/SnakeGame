package wang.qrwells.net.udp;

import wang.qrwells.message.AbstractMessage;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

public class UDPMessageWriter {
  public byte[] write(AbstractMessage message) {
    var baos = new ByteArrayOutputStream();
    try (var out = new ObjectOutputStream(baos)) {
      out.writeObject(message);
      return baos.toByteArray();
    } catch (IOException e) {
      e.printStackTrace();
      throw new RuntimeException(e);
    }
  }
}
