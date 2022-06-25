package wang.qrwells;

import org.junit.jupiter.api.Test;
import wang.qrwells.net.Client;
import wang.qrwells.net.tcp.TCPClient;

public class ClientTest {
  public static void main(String[] args) {
    var client = new TCPClient();
    client.setHost("localhost").setPort(8080);
    client.connect();

    client.setOnConnected((c) -> System.out.println("connected"));
    client.setOnDisconnected((c) -> System.out.println("disconnected"));
  }
}
