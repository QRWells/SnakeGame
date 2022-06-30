package wang.qrwells.net.server.channel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import wang.qrwells.net.server.handler.ChannelReadHandler;
import wang.qrwells.net.server.handler.impl.ChannelInitHandler;
import wang.qrwells.net.server.pipeline.Interface.MessageHandlerContext;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.StandardSocketOptions;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

public class NioServerChannel extends Channel {
  private static final Logger logger = LoggerFactory.getLogger(NioServerChannel.class);

  public NioServerChannel(NioEventLoop nioEventLoop) throws IOException {
    super(null, nioEventLoop, ServerSocketChannel.open());
    javaChannel.configureBlocking(false);
  }

  @Override
  public void register() {
    eventLoop.registerInterest(this, SelectionKey.OP_ACCEPT);
  }

  @Override
  protected Object read0() throws IOException {
    return ((ServerSocketChannel) javaChannel).accept();
  }

  @Override
  public void write(Object msg) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void flush() {
    throw new UnsupportedOperationException();
  }

  @Override
  public ServerSocketChannel javaChanel() {
    return (ServerSocketChannel) javaChannel;
  }

  public void listen(int port) throws IOException {
    javaChanel().bind(new InetSocketAddress(port));
  }

  public static class AcceptorHandler implements ChannelReadHandler {
    private static final Logger logger = LoggerFactory.getLogger(AcceptorHandler.class);
    private final NioEventLoopGroup worker;
    private final NioServerChannel parentChannel;
    private final ChannelInitHandler channelInitHandler;

    public AcceptorHandler(NioEventLoopGroup worker, NioServerChannel parentChannel,
                           ChannelInitHandler channelInitHandler) {
      this.worker = worker;
      this.parentChannel = parentChannel;
      this.channelInitHandler = channelInitHandler;
    }

    @Override
    public void read(MessageHandlerContext mhc, Object msg) throws Throwable {
      logger.info("Connected from {}", ((SocketChannel) msg).getRemoteAddress());
      SocketChannel socketChannel = (SocketChannel) msg;
      socketChannel.configureBlocking(false);
      socketChannel.setOption(StandardSocketOptions.SO_KEEPALIVE, true);
//      socketChannel.setOption(StandardSocketOptions.TCP_NODELAY, true);
      NioEventLoop selectedEventLoop = worker.getEventLoop();
      NioByteChannel channel = new NioByteChannel(parentChannel, selectedEventLoop, socketChannel,
                                                  SelectionKey.OP_READ);
      channel.register();
      channel.pipeline.addLast(channelInitHandler);
      mhc.nextRead(channel);
    }
  }
}
