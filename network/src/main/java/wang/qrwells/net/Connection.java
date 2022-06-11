package wang.qrwells.net;

import javafx.application.Platform;
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
  protected final List<MessageHandler> messageHandlersFX = new ArrayList<>();
  private final int connectionNum;
  protected BlockingQueue<AbstractMessage> messageQueue =
      new ArrayBlockingQueue<>(100);
  private boolean isConnected = true;
  private boolean isJavaFXExceptionLogged = false;

  public Connection(int connectionNum) {
    this.connectionNum = connectionNum;
  }

  public final int getConnectionNum() {
    return connectionNum;
  }

  public final <T extends AbstractMessage> void addMessageHandler(
      MessageHandler<T> handler) {
    messageHandlers.add(handler);
  }

  public final <T extends AbstractMessage> void removeMessageHandler(
      MessageHandler<T> handler) {
    messageHandlers.remove(handler);
  }

  public final <T extends AbstractMessage> void addMessageHandlerFX(
      MessageHandler<T> handler) {
    messageHandlersFX.add(handler);
  }

  public final void removeMessageHandlerFX(
      MessageHandler<AbstractMessage> handler) {
    messageHandlersFX.remove(handler);
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

      try {
        Platform.runLater(
            () -> messageHandlersFX.forEach(h -> h.onReceive(this, message)));
      } catch (IllegalStateException e) {
        // if javafx is not initialized then ignore
        if (!isJavaFXExceptionLogged) {
          log.log(Level.WARNING,
                  "JavaFX is not initialized to handle messages on FX " +
                  "thread", e);
          isJavaFXExceptionLogged = true;
        }
      }
    } catch (Exception e) {
      log.log(Level.WARNING, "Exception during MessageHandler.onReceive()", e);
    }
  }

  public final void terminate() {
    if (!isConnected()) {
      log.warning("Attempted to close connection " + connectionNum + " but it" +
                  " is already closed.");
      return;
    }
    log.info("Closing connection " + connectionNum);

    try {
      terminateImpl();

      log.info("Connection " + connectionNum + " was correctly closed from " +
               "local endpoint.");
    } catch (Exception e) {
      log.log(Level.WARNING, "Error during terminateImpl()", e);
    }

    isConnected = false;
  }

  protected abstract boolean isClosedLocally();

  protected abstract void terminateImpl() throws Exception;
}
