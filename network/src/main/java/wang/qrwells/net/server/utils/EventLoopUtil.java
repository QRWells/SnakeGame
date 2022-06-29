package wang.qrwells.net.server.utils;


import wang.qrwells.net.server.channel.Channel;

public class EventLoopUtil {
  public static boolean inEventLoop(Channel channel) {
    return channel.eventLoop() == Thread.currentThread();
  }

  public static void runOnContext(Channel channel, Runnable runnable) {
    channel.eventLoop().submit(runnable);
  }

  public static void runOnContextInstant(Channel channel, Runnable runnable) {
    if (inEventLoop(channel)) {
      runnable.run();
      return;
    }
    runOnContext(channel, runnable);
  }
}
