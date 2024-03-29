package wang.qrwells.net.client.udp;

import wang.qrwells.net.client.Connection;

import java.net.DatagramSocket;
import java.util.concurrent.ArrayBlockingQueue;

public class UDPConnection extends Connection {
  private final ArrayBlockingQueue<byte[]> queue = new ArrayBlockingQueue<>(32);
  private DatagramSocket socket;
  private String ip;
  private int port;
  private int bufferSize;

  public void sendUDP(byte[] bytes) {

  }

  public ArrayBlockingQueue<byte[]> getRecvQueue() {
    return queue;
  }

  public void receive(byte[] data) {

  }

  @Override
  protected boolean isClosedLocally() {
    return false;
  }

  @Override
  protected void terminateImpl() throws Exception {

  }
}
