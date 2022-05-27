package wang.qrwells.net.udp;

import wang.qrwells.net.Message;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;

public class UDPMessageReader {
  public Message read(byte[] bytes) {
    try (var in = new ObjectInputStream(new ByteArrayInputStream(bytes))) {
      return (Message) in.readObject();
    } catch (Exception e) {
      e.printStackTrace();
      throw new RuntimeException(e);
    }
  }
}
