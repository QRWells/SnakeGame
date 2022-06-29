package wang.qrwells.net.client.tcp;

import wang.qrwells.net.client.Connection;

import java.net.Socket;

public class TCPConnection extends Connection {
  private final Socket socket;

  public TCPConnection(Socket socket) {
    this.socket = socket;
  }

  public Socket getSocket() {
    return socket;
  }

  @Override
  protected boolean isClosedLocally() {
    return socket.isClosed();
  }

  @Override
  protected void terminateImpl() throws Exception {
    // closing socket auto-closes in and out streams
    socket.close();
  }
}
