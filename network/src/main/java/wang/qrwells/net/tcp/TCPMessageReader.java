package wang.qrwells.net.tcp;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

public class TCPMessageReader {
  private final DataInputStream stream;

  public TCPMessageReader(InputStream stream) {
    this.stream = new DataInputStream(stream);
  }

  public byte[] read() throws IOException {
    var len = stream.readInt();
    var buffer = new byte[len];
    var bytesReadSoFar = 0;
    while (bytesReadSoFar != len) {
      var bytesReadNow = stream.read(buffer, bytesReadSoFar,
                                     len - bytesReadSoFar);
      bytesReadSoFar += bytesReadNow;
    }

    return buffer;
  }
}
