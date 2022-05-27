package wang.qrwells.net;

import javafx.application.Platform;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public abstract class Connection {
  protected static final Logger log = LogManager.getLogger(Connection.class);
  protected final List<MessageHandler> messageHandlers = new ArrayList<>();
  protected final List<MessageHandler> messageHandlersFX = new ArrayList<>();
  private final int connectionNum;
  protected BlockingQueue<Message> messageQueue = new ArrayBlockingQueue<>(100);
  private boolean isConnected = true;
  private boolean isJavaFXExceptionLogged = false;

  public Connection(int connectionNum) {
    this.connectionNum = connectionNum;
  }

  public final int getConnectionNum() {
    return connectionNum;
  }

  public final void addMessageHandler(MessageHandler handler) {
    messageHandlers.add(handler);
  }

  public final void removeMessageHandler(MessageHandler handler) {
    messageHandlers.remove(handler);
  }

  public final void addMessageHandlerFX(MessageHandler handler) {
    messageHandlersFX.add(handler);
  }

  public final void removeMessageHandlerFX(MessageHandler handler) {
    messageHandlersFX.remove(handler);
  }

  public final void send(Message message) {
    if (!isConnected()) {
      log.warn("Attempted to send but connection is not connected");
      return;
    }

    try {
      messageQueue.put(message);
    } catch (InterruptedException e) {
      log.warn(
          "send() was interrupted while waiting for messageQueue to clear " +
          "some space", e);
    }
  }

  public boolean isConnected() {
    return isConnected;
  }

  public void notifyMessageReceived(Message message) {
    // exceptions here should only occur if they were thrown at user level
    // during handling messages via onReceive()

    try {
      messageHandlers.forEach(h -> h.onReceive(this, message));

      try {
        Platform.runLater(
            () -> messageHandlersFX.forEach(h -> h.onReceive(this, message)));
      } catch (IllegalStateException e) {
        // if javafx is not initialized then ignore
        if (!isJavaFXExceptionLogged) {
          log.warn("JavaFX is not initialized to handle messages on FX thread",
                   e);
          isJavaFXExceptionLogged = true;
        }
      }
    } catch (Exception e) {
      log.warn("Exception during MessageHandler.onReceive()", e);
    }
  }

  public final void terminate() {
    if (!isConnected()) {
      log.warn("Attempted to close connection " + connectionNum + " but it" +
               " is already closed.");
      return;
    }

    log.debug("Closing connection " + connectionNum);

    try {
      terminateImpl();

      log.debug("Connection " + connectionNum + " was correctly closed from " +
                "local endpoint.");
    } catch (Exception e) {
      log.warn("Error during terminateImpl()", e);
    }

    isConnected = false;
  }

  protected abstract boolean isClosedLocally();

  protected abstract void terminateImpl() throws Exception;
}
