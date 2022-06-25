package wang.qrwells.net;

import wang.qrwells.message.AbstractMessage;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class Connection {
  protected static final Logger log = Logger.getLogger(
      Connection.class.getName());
  protected final List<MessageHandler> messageHandlers = new ArrayList<>();
  protected BlockingQueue<AbstractMessage> messageQueue =
      new ArrayBlockingQueue<>(100);
  private boolean isConnected = true;

  public final <T extends AbstractMessage> void addMessageHandler(
      MessageHandler<T> handler) {
    messageHandlers.add(handler);
  }

  public final <T extends AbstractMessage> void removeMessageHandler(
      MessageHandler<T> handler) {
    messageHandlers.remove(handler);
  }

  public final void send(AbstractMessage message) {
    if (!isConnected()) {
      log.warning("Attempted to send but connection is not connected");
      return;
    }

    try {
      messageQueue.put(message);
    } catch (InterruptedException e) {
      log.log(Level.WARNING,
              "send() was interrupted while waiting for messageQueue to clear" +
              " some space", e);
    }
  }

  public boolean isConnected() {
    return isConnected;
  }

  public void notifyMessageReceived(AbstractMessage message) {
    // exceptions here should only occur if they were thrown at user level
    // during handling messages via onReceive()

    try {
      messageHandlers.forEach(h -> h.onReceive(this, message));
    } catch (Exception e) {
      log.log(Level.WARNING, "Exception during MessageHandler.onReceive()", e);
    }
  }

  public final void terminate() {
    if (!isConnected()) {
      log.warning("Attempted to close connection but it is already closed.");
      return;
    }
    log.info("Closing connection");

    try {
      terminateImpl();

      log.info("Connection was correctly closed from local endpoint.");
    } catch (Exception e) {
      log.log(Level.WARNING, "Error during terminateImpl()", e);
    }

    isConnected = false;
  }

  protected abstract boolean isClosedLocally();

  protected abstract void terminateImpl() throws Exception;
}
