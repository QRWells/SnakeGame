package wang.qrwells.net.udp;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import wang.qrwells.net.Client;

import java.io.IOException;
import java.net.*;
import java.util.Arrays;

public class UDPClient extends Client {
  private static final byte[] MSG_OPEN = new byte[]{
      -2, -1, 0, 70, 0, 88, 0, 71, 0, 76, 0, 95, 0, 72, 0, 69, 0, 76, 0, 76, 0,
      79
  };
  private static final byte[] MSG_CLOSE = new byte[]{
      -2, -1, 0, 70, 0, 88, 0, 71, 0, 76, 0, 95, 0, 66, 0, 89, 0, 69, 0, 33, 0,
      33
  };
  private final String ip;
  private final int port;
  private final Logger log = LogManager.getLogger(UDPClient.class);
  private boolean isStopped = false;
  private DatagramSocket socket = null;

  public UDPClient(String ip, int port) {
    this.ip = ip;
    this.port = port;
  }

  @Override
  protected void connect0() throws SocketException {
    try (var _socket = new DatagramSocket()) {
      socket = _socket;
      _socket.connect(InetAddress.getByName(ip), port);
      var connection = (UDPConnection) getConnection();
      if (connection == null) {
        connection = new UDPConnection(1);
        openUDPConnection(connection);
        var packet = new DatagramPacket(MSG_OPEN, MSG_OPEN.length);
        _socket.send(packet);
      }

      var buffer = new byte[2048];

      while (!isStopped) {
        Arrays.fill(buffer, (byte) 0);
        var packet = new DatagramPacket(buffer, buffer.length);
        _socket.receive(packet);
        var isClose = Arrays.equals(
            Arrays.copyOfRange(packet.getData(), 0, MSG_CLOSE.length),
            MSG_CLOSE);
        if (isClose) {
          log.info("close connection");
          isStopped = true;
          onConnectionClosed(connection);
        } else {
          connection.receive(packet.getData());
        }
      }

      if (_socket.isClosed()) {
        onConnectionClosed(connection);
      }
    } catch (UnknownHostException e) {
      e.printStackTrace();
      throw new RuntimeException(e);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void disconnect() {
    if (isStopped) {
      log.warn("Attempted to stop a client that is already stopped");
      return;
    }
    isStopped = true;
    getConnection().terminate();
    try {
      socket.close();
    } catch (Exception e) {
      log.warn("Exception when closing client socket: " + e.getMessage(), e);
    }
  }
}
