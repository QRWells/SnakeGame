package wang.qrwells.net.tcp;

import wang.qrwells.net.Client;

import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.logging.Logger;

public class TCPClient extends Client {
  private static final Logger log = Logger.getLogger(TCPClient.class.getName());
  public boolean connected = false;
  private String host = "localhost";
  private int port = 8080;

  public TCPClient(String host, int port) {
    this.host = host;
    this.port = port;
  }

  public TCPClient() {
  }

  public TCPClient(InetSocketAddress address) {
    this(address.getHostString(), address.getPort());
  }

  public TCPClient setHost(String host) {
    this.host = host;
    return this;
  }

  public TCPClient setPort(int port) {
    this.port = port;
    return this;
  }

  @Override
  public void connect() {
    log.info("Connecting to " + host + ":" + port);
    Socket socket;
    try {
      socket = new Socket(host, port);
      log.info("Created socket to " + host + ":" + port);
    } catch (Exception e) {
      throw new RuntimeException(
          "Failed to create a socket to address " + host + " : " + port +
          " Error: " + e, e);
    }
    try {
      openTCPConnection(socket);
      connected = true;
    } catch (Exception e) {
      disconnect();
      throw new RuntimeException(
          "Failed to open TCP connection to " + host + ":" + port + " Error: " +
          e, e);
    }
  }

  @Override
  public void disconnect() {
    var connection = getConnection();
    if (connection != null) {
      connection.terminate();
      connected = false;
    }
  }
}
