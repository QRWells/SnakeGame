package wang.qrwells.net.server.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import wang.qrwells.net.server.channel.NioEventLoopGroup;
import wang.qrwells.net.server.channel.NioServerChannel;
import wang.qrwells.net.server.handler.impl.ChannelInitHandler;

import java.io.IOException;

public class TCPServer {
  private static final Logger logger = LoggerFactory.getLogger(TCPServer.class);
  private NioEventLoopGroup acceptorGroup;
  private NioEventLoopGroup workerGroup;
  private ChannelInitHandler channelInitHandler;

  private int port = 8888;

  public TCPServer group(NioEventLoopGroup acceptorGroup,
                         NioEventLoopGroup workerGroup) {
    this.acceptorGroup = acceptorGroup;
    this.workerGroup = workerGroup;
    return this;
  }

  public TCPServer init(ChannelInitHandler channelInitHandler) {
    this.channelInitHandler = channelInitHandler;
    return this;
  }

  public TCPServer bind(int port) {
    this.port = port;
    return this;
  }

  public void start() throws IOException {
    logger.info("TCP Server start at port: " + port);
    acceptorGroup.start();
    workerGroup.start();
    var serverChannel = new NioServerChannel(acceptorGroup.getEventLoop());
    serverChannel.listen(port);
    serverChannel.register();
    serverChannel.getPipeline()
                 .addLast(new NioServerChannel.AcceptorHandler(workerGroup,
                                                               serverChannel,
                                                               channelInitHandler));
  }
}
