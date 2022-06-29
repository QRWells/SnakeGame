package wang.qrwells.net.server.handler;

import wang.qrwells.net.server.pipeline.Interface.MessageHandlerContext;

public interface ChannelHandler {

  default void onException(MessageHandlerContext mhc, Throwable e) {
    mhc.nextExceptionHandler(e);
  }

  default void onAdded(MessageHandlerContext mhc) {
  }

  default void onRemove(MessageHandlerContext mhc) {
  }

  default void onActive(MessageHandlerContext mhc) throws Throwable {

    mhc.nextActive();
  }

  default void onInactive(MessageHandlerContext mhc) throws Throwable {
    mhc.nextInactive();
  }
}
