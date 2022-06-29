package wang.qrwells;


import wang.qrwells.handler.codec.*;
import wang.qrwells.handler.*;
import wang.qrwells.net.server.channel.NioEventLoopGroup;
import wang.qrwells.net.server.handler.impl.ChannelInitHandler;
import wang.qrwells.net.server.pipeline.Pipeline;
import wang.qrwells.net.server.server.TCPServer;

public class Server {
  public static void main(String[] args) throws Exception {
    var bossGroup = new NioEventLoopGroup(1);
    var workerGroup = new NioEventLoopGroup(Runtime.getRuntime()
                                                   .availableProcessors() * 2);
    var initHandler = new ChannelInitHandler() {
      @Override
      public void init(Pipeline pipeline) {
        pipeline.addLast(new ByteToMessageDecoder());

        pipeline.addLast(new RegisterMessageHandler());
        pipeline.addLast(new LoginMessageHandler());
        pipeline.addLast(new LogoutMessageHandler());
        pipeline.addLast(new CreateGroupMessageHandler());
        pipeline.addLast(new JoinToGroupMessageHandler());
        pipeline.addLast(new IdRequestHandler());
        pipeline.addLast(new ChatMessageHandler());
        pipeline.addLast(new SnakeGameMessageHandler());

        pipeline.addLast(new MessageToByteEncoder());
      }
    };
    var tcpServer = new TCPServer().init(initHandler)
                                   .group(bossGroup, workerGroup)
                                   .bind(8888);
    tcpServer.start();
  }
}