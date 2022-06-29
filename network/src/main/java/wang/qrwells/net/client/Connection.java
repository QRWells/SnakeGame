package wang.qrwells.net.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import wang.qrwells.message.AbstractMessage;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

public abstract class Connection {
  protected static final Logger log = LoggerFactory.getLogger(Connection.class);
  protected final ConcurrentHashMap<String, MessageHandler> messageHandlers =
      new ConcurrentHashMap<>();
  protected BlockingQueue<AbstractMessage> messageQueue =
      new ArrayBlockingQueue<>(100);
  private boolean isConnected = true;

  public final void addMessageHandler(String name, MessageHandler handler) {
    log.info("Adding message handler.");
    messageHandlers.put(name, handler);
  }

  public final void removeMessageHandler(String name) {
    log.info("Removing message handler.");
    messageHandlers.remove(name);
  }

  public final void clearMessageHandlers() {
    log.info("Clearing message handlers.");
    messageHandlers.clear();
  }

  public final void send(AbstractMessage message) {
    if (!isConnected()) {
      log.warn("Attempted to send but connection is not connected");
      return;
    }

    try {
      messageQueue.put(message);
    } catch (InterruptedException e) {
      log.warn(
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
    log.info(
        "Received message with length: " + message.getLength() + " and type: " +
        message.getClass()
               .getName());
    try {
      if (messageHandlers.size() != 0)
        messageHandlers.forEach((n, h) -> h.onReceive(this, message));
    } catch (Exception e) {
      log.warn("Exception during MessageHandler.onReceive()", e);
    }
  }

  public final void terminate() {
    if (!isConnected()) {
      log.warn("Attempted to close connection but it is already closed.");
      return;
    }
    log.info("Closing connection");

    try {
      terminateImpl();

      log.info("Connection was correctly closed from local endpoint.");
    } catch (Exception e) {
      log.warn("Error during terminateImpl()", e);
    }

    isConnected = false;
  }

  protected abstract boolean isClosedLocally();

  protected abstract void terminateImpl() throws Exception;
}
