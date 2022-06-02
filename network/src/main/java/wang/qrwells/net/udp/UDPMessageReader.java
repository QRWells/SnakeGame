package wang.qrwells.net.udp;

import wang.qrwells.message.AbstractMessage;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;

public class UDPMessageReader {
  public AbstractMessage read(byte[] bytes) {
    try (var in = new ObjectInputStream(new ByteArrayInputStream(bytes))) {
      return (AbstractMessage) in.readObject();
    } catch (Exception e) {
      e.printStackTrace();
      throw new RuntimeException(e);
    }
  }
}
