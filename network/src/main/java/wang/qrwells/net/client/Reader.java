package wang.qrwells.net.client;

import wang.qrwells.net.client.tcp.TCPMessageReader;
import wang.qrwells.net.client.udp.UDPMessageReader;

import java.io.InputStream;

public class Reader {
  public static TCPMessageReader getTCPReader(InputStream in) {
    return new TCPMessageReader(in);
  }

  public static UDPMessageReader getUDPReader() {
    return new UDPMessageReader();
  }
}
