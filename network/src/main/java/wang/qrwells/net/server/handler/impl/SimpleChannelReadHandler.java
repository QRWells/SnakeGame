package wang.qrwells.net.server.handler.impl;

import wang.qrwells.net.server.handler.ChannelReadHandler;
import wang.qrwells.net.server.pipeline.Interface.MessageHandlerContext;
import wang.qrwells.net.server.utils.TypeMatcher;

public abstract class SimpleChannelReadHandler<I> implements
    ChannelReadHandler {
  private final TypeMatcher matcher;

  protected SimpleChannelReadHandler(Class<? extends I> t) {
    this.matcher = new TypeMatcher(t);
  }

  public abstract void read0(MessageHandlerContext ctx, I msg) throws Throwable;

  @Override
  public void read(MessageHandlerContext mhc, Object msg) throws Throwable {
    if (matcher.match(msg)) {
      @SuppressWarnings("unchecked") I i = (I) msg;
      read0(mhc, i);
    } else {
      mhc.nextRead(msg);
    }
  }
}
