package wang.qrwells.net.server.pipeline;

import wang.qrwells.net.server.bytemsg.msg.ByteMsg;
import wang.qrwells.net.server.channel.Channel;
import wang.qrwells.net.server.exception.UnCaughtException;
import wang.qrwells.net.server.handler.ChannelHandler;
import wang.qrwells.net.server.handler.ChannelReadHandler;
import wang.qrwells.net.server.handler.ChannelWriteHandler;
import wang.qrwells.net.server.pipeline.Interface.HandlerNode;
import wang.qrwells.net.server.pipeline.Interface.MessageHandlerContext;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.util.ArrayDeque;

public class TailNode extends HandlerNode {

  private final ArrayDeque<ByteMsg> unflushMsg;
  private int unflush;
  private final int maxSpin;

  public TailNode(Pipeline pipeline) {
    super(pipeline, new TailNodeHandler());
    unflush = 0;
    unflushMsg = new ArrayDeque<>();
    maxSpin = 64;
    ((TailNodeHandler) getHandler()).bindTailNode(this);
  }

  @Override
  public boolean isReadHandler() {
    return true;
  }

  @Override
  public boolean isWriteHandler() {
    return true;
  }

  @Override
  public ChannelHandler getHandler() {
    return channelHandler;
  }

  @Override
  public Pipeline getPipeline() {
    return pipeline;
  }

  @Override
  public HandlerNode next() {
    return null;
  }


  @Override
  public HandlerNode pre() {
    return pre;
  }

  @Override
  public void setPre(HandlerNode handlerNode) {
    pre = handlerNode;
  }

  @Override
  public void setNext(HandlerNode handlerNode) {

  }

  public void flush() throws IOException {
    Channel channel = pipeline.getChannel();
    while (!unflushMsg.isEmpty()) {
      int hasWrite = 0;
      ByteMsg wait = unflushMsg.peek();
      int spin = 0;
      while (spin < maxSpin) {
        int i = wait.writeToChannel(channel);
        hasWrite += i;
        if (hasWrite == wait.size()) break;
        if (i == 0) spin++;
      }
      unflush -= hasWrite;
      //write out all
      if (spin < maxSpin) {
        wait.release();
        channel.eventLoop().removeInterest(channel, SelectionKey.OP_WRITE);
        unflushMsg.removeFirst();
        continue;
      }
      channel.eventLoop().addInterest(channel, SelectionKey.OP_WRITE);
      break;
    }
  }

  private static class TailNodeHandler implements ChannelReadHandler,
      ChannelWriteHandler {

    private TailNode tailNode;

    public void bindTailNode(TailNode node) {
      tailNode = node;
    }

    @Override
    public void onException(MessageHandlerContext mhc, Throwable e) {
      throw new UnCaughtException(e);
    }

    @Override
    public void write(MessageHandlerContext mhc, Object msg) {
      if (!(msg instanceof ByteMsg)) {
        throw new IllegalArgumentException(
            "Data is not cast to subclass of " + "ByteMsg");
      }
      tailNode.unflushMsg.offer((ByteMsg) msg);
      tailNode.unflush += ((ByteMsg) msg).size();
    }

    @Override
    public void read(MessageHandlerContext mhc, Object msg) {
      if (msg instanceof ByteMsg) {
        ((ByteMsg) msg).release();
      }
      if (msg instanceof Channel) {
        return;
      }
      throw new IllegalArgumentException("Data hasn't been consumed");
    }

    @Override
    public void onActive(MessageHandlerContext mhc) throws Throwable {

    }

    @Override
    public void onInactive(MessageHandlerContext mhc) throws Throwable {

    }
  }
}
