package wang.qrwells.net.client.tcp;

import wang.qrwells.message.AbstractMessage;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class TCPMessageWriter {
  private final DataOutputStream out;

  public TCPMessageWriter(OutputStream out) {
    this.out = new DataOutputStream(out);
  }

  public void write(AbstractMessage message) throws IOException {
    var bytes = message.getBytes();
    out.writeInt(bytes.length);
    out.write(bytes);
    out.flush();
  }
}
