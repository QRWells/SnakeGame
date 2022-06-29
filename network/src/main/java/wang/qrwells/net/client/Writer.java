package wang.qrwells.net.client;

import wang.qrwells.net.client.tcp.TCPMessageWriter;
import wang.qrwells.net.client.udp.UDPMessageWriter;

import java.io.OutputStream;

public class Writer {
  public static TCPMessageWriter getTCPWriter(OutputStream outputStream) {
    return new TCPMessageWriter(outputStream);
  }

  public static UDPMessageWriter getUDPWriter() {
    return new UDPMessageWriter();
  }
}
