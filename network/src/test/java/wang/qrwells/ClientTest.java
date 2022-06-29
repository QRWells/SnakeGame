package wang.qrwells;

import org.junit.jupiter.api.Test;
import wang.qrwells.message.impl.LoginMessage;
import wang.qrwells.net.client.tcp.TCPClient;

public class ClientTest {
  @Test
  void InitTest() throws InterruptedException {
    var client = new TCPClient();
    client.setHost("localhost")
          .setPort(8888);
    client.connect();

    client.setOnConnected((c) -> {
      System.out.println("Connected");
    });
    client.setOnDisconnected((c) -> System.out.println("disconnected"));

    if (client.connected) {
      client.getConnection().addMessageHandler("init",(c, msg) -> {
        System.out.println(msg);
      });
      client.getConnection()
            .send(new LoginMessage("test", "test"));
    }

    Thread.sleep(1000 * 10);
    client.disconnect();
  }
}
