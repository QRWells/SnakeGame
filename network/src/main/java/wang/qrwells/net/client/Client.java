package wang.qrwells.net.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import wang.qrwells.message.MessageUtil;
import wang.qrwells.net.client.tcp.TCPConnection;
import wang.qrwells.net.client.udp.UDPConnection;

import java.io.EOFException;
import java.net.Socket;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.util.function.Consumer;

public abstract class Client {
  private static final Logger log = LoggerFactory.getLogger(Client.class);

  private Connection connection;

  private Consumer<Connection> onConnected = c -> {
  };
  private Consumer<Connection> onDisconnected = c -> {
  };

  public final void setOnConnected(Consumer<Connection> onConnected) {
    this.onConnected = onConnected;
  }

  public final void setOnDisconnected(Consumer<Connection> onDisconnected) {
    this.onDisconnected = onDisconnected;
  }

  protected final void openTCPConnection(Socket socket) throws Exception {
    log.info(getClass().getSimpleName() + " opening new connection from " + socket.getInetAddress() + ":" +
             socket.getPort());

    socket.setTcpNoDelay(true);
    socket.setKeepAlive(true);

    Connection connection = new TCPConnection(socket);

    onConnectionOpened(connection);

    var sendThreadName = getClass().getSimpleName() + "_SendThread";
    var recvThreadName = getClass().getSimpleName() + "_RecvThread";

    new ConnectionThread(sendThreadName, () -> {

      try {
        var writer = Writer.getTCPWriter(socket.getOutputStream());

        while (connection.isConnected()) {
          var message = connection.messageQueue.take();
          writer.write(message);
        }
      } catch (Exception e) {
        log.warn(sendThreadName + " crashed", e);
      }
    }).start();

    new ConnectionThread(recvThreadName, () -> {
      try {
        var reader = Reader.getTCPReader(socket.getInputStream());

        while (connection.isConnected()) {
          try {
            var message = reader.read();
            var buffer = ByteBuffer.wrap(message);
            connection.notifyMessageReceived(MessageUtil.parseMessage(buffer));

          } catch (EOFException e) {
            log.info("Connection was correctly closed from remote endpoint.");

            connection.terminate();
          } catch (SocketException e) {
            if (!connection.isClosedLocally()) {
              log.info("Connection was unexpectedly disconnected: " + e.getMessage());
              connection.terminate();
            }
          } catch (Exception e) {
            log.warn("Connection had unspecified error during receive()", e);
            connection.terminate();
          }
        }
      } catch (Exception e) {
        log.warn(recvThreadName + " crashed", e);
      }

      onConnectionClosed(connection);
    }).start();
  }

  protected final void openUDPConnection(UDPConnection connection) {
    log.info("Opening UDP connection.");

    onConnectionOpened(connection);

    var sendThreadName = getClass().getSimpleName() + "_SendThread";
    var recvThreadName = getClass().getSimpleName() + "_RecvThread";

    new ConnectionThread(sendThreadName, () -> {

      try {
        while (connection.isConnected()) {
          var message = connection.messageQueue.take();

          var bytes = Writer.getUDPWriter().write(message);

          connection.sendUDP(bytes);
        }
      } catch (Exception e) {
        log.warn(sendThreadName + " crashed", e);
      }
    }).start();

    new ConnectionThread(recvThreadName, () -> {

      try {
        var reader = Reader.getUDPReader();

        while (connection.isConnected()) {
          var bytes = connection.getRecvQueue().take();

          var message = reader.read(bytes);

          connection.notifyMessageReceived(message);
        }
      } catch (Exception e) {
        log.warn(recvThreadName + " crashed", e);
      }
    }).start();
  }

  private void onConnectionOpened(Connection connection) {
    log.info(getClass().getSimpleName() + " successfully opened connection.");
    this.connection = connection;
    try {
      onConnected.accept(connection);
    } catch (Exception e) {
      log.warn("Exception occurred in onConnected callback", e);
    }
  }

  protected final void onConnectionClosed(Connection connection) {
    log.info(getClass().getSimpleName() + " connection was closed");
    onDisconnected.accept(connection);
    this.connection = null;
  }

  public final Connection getConnection() {
    return connection;
  }

  public abstract void connect();

  public abstract void disconnect();

  private static class ConnectionThread extends Thread {

    ConnectionThread(String name, Runnable action) {
      super(action, name);
      setDaemon(true);
    }
  }
}
