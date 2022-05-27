package wang.qrwells.net;

import wang.qrwells.net.tcp.TCPMessageWriter;
import wang.qrwells.net.udp.UDPMessageWriter;

import java.io.OutputStream;

public class Writer {
  public static TCPMessageWriter getTCPWriter(OutputStream outputStream) {
    return new TCPMessageWriter(outputStream);
  }

  public static UDPMessageWriter getUDPWriter() {
    return new UDPMessageWriter();
  }
}
