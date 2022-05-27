package wang.qrwells.net;

import wang.qrwells.net.tcp.TCPMessageReader;
import wang.qrwells.net.udp.UDPMessageReader;

import java.io.InputStream;

public class Reader {
  public static TCPMessageReader getTCPReader(InputStream in) {
    return new TCPMessageReader(in);
  }

  public static UDPMessageReader getUDPReader() {
    return new UDPMessageReader();
  }
}
