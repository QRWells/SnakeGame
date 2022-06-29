package wang.qrwells.net.server.pipeline.Interface;


import wang.qrwells.net.server.channel.Channel;
import wang.qrwells.net.server.pipeline.Pipeline;

public interface MessageHandlerContext {
  Channel channel();

  Pipeline pipeline();

  void nextRead(Object msg) throws Throwable;

  void nextWrite(Object msg) throws Throwable;

  void nextExceptionHandler(Throwable t);

  void nextActive() throws Throwable;

  void nextInactive() throws Throwable;
}
