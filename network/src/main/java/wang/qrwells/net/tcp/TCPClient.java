package wang.qrwells.net.tcp;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import wang.qrwells.net.Client;

import java.net.Socket;

public class TCPClient extends Client {
  private static final Logger log = LogManager.getLogger(TCPClient.class);

  private final String ip;
  private final int port;

  public TCPClient(String ip, int port) {
    this.ip = ip;
    this.port = port;
  }

  @Override
  public void connect0() {
    log.debug("Connecting to " + ip + ":" + port);
    Socket socket;
    try {
      socket = new Socket(ip, port);
      log.debug("Created socket to " + ip + ":" + port);
    } catch (Exception e) {
      throw new RuntimeException(
          "Failed to create a socket to address " + ip + " : " + port +
          " Error: " + e, e);
    }
    try {
      openTCPConnection(socket);
    } catch (Exception e) {
      disconnect();
      throw new RuntimeException(
          "Failed to open TCP connection to " + ip + ":" + port + " Error: " +
          e, e);
    }
  }

  @Override
  public void disconnect() {
    getConnection().terminate();
  }
}
