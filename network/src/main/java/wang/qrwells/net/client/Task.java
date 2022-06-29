package wang.qrwells.net.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Callable;
import java.util.function.Consumer;
import java.util.function.Function;

public abstract class Task<T> {
  private static final Logger log = LoggerFactory.getLogger(Task.class);

  private static final String DEFAULT_NAME = "NoName";

  private final String name;

  private Consumer<T> successAction = (result) -> {
  };
  private Consumer<Throwable> failAction = (e) -> log.warn(
      getName() + " failed", e);
  private Runnable cancelAction = () -> {

  };

  private boolean hasFailAction = false;

  private boolean isCancelled = false;

  public Task() {
    this(DEFAULT_NAME);
  }

  public Task(String name) {
    this.name = name;
  }

  public static Task<Void> ofVoid(Runnable action) {
    return ofVoid(DEFAULT_NAME, action);
  }

  public static Task<Void> ofVoid(String name, Runnable action) {
    return of(name, () -> {
      action.run();
      return null;
    });
  }

  public static <R> Task<R> of(Callable<R> action) {
    return of(DEFAULT_NAME, action);
  }

  public static <R> Task<R> of(String name, Callable<R> action) {
    return new Task<>(name) {
      @Override
      protected R onExecute() throws Exception {
        return action.call();
      }
    };
  }

  public final String getName() {
    return name;
  }

  public final boolean hasFailAction() {
    return hasFailAction;
  }

  /**
   * @return whether this task was cancelled by calling "cancel()"
   */
  public boolean isCancelled() {
    return isCancelled;
  }

  /**
   * Cancel the execution of this task.
   * No-op if the task already completed.
   * It is up to each individual task to decide what to do when cancelled.
   * Tasks are free to ignore cancellation and result in onSuccess or
   * onFailure as normal.
   * The tasks that honor cancellation must call throwCancelException(),
   * which will be caught
   * and cancelAction invoked.
   */
  public void cancel() {
    isCancelled = true;
  }

  /**
   * Set consumer action for success scenario.
   *
   * @param successAction action to call if the task succeeds
   */
  public final Task<T> onSuccess(Consumer<T> successAction) {
    this.successAction = successAction;
    return this;
  }

  /**
   * Set error consumer for fail scenario.
   *
   * @param failAction exception handler to call if the task fails
   */
  public final Task<T> onFailure(Consumer<Throwable> failAction) {
    hasFailAction = true;
    this.failAction = failAction;
    return this;
  }

  /**
   * Set action to call on cancel.
   */
  public final Task<T> onCancel(Runnable cancelAction) {
    this.cancelAction = cancelAction;
    return this;
  }

  protected abstract T onExecute() throws Exception;

  protected void throwCancelException() {
    throw new IOTaskCancelledException();
  }

  /**
   * Executes this task synchronously on this thread.
   * All callbacks will be called on this thread.
   */
  public final T run() {
    try {
      if (isCancelled)
        throwCancelException();

      T value = onExecute();
      succeed(value);
      return value;
    } catch (Exception e) {
      fail(e);
      return null;
    }
  }

  /**
   * Allows chaining IO tasks to be executed sequentially.
   *
   * @param mapper function that takes result of previous task and returns
   *               new task
   * @return IO task
   */
  public final <R> Task<R> then(Function<T, Task<R>> mapper) {
    return of(name, () -> mapper.apply(onExecute())
                                .onExecute());
  }

  /* Static convenience methods */

  public final <R> Task<R> thenWrap(Function<T, R> mapper) {
    return then(t -> of(() -> mapper.apply(t)));
  }

  private void succeed(T result) {
    successAction.accept(result);
  }

  private void fail(Throwable error) {
    if (error instanceof IOTaskCancelledException) {
      cancelAction.run();
      return;
    }

    failAction.accept(error);
  }

  private static class IOTaskCancelledException extends RuntimeException {

    IOTaskCancelledException() {
      super("IOTask was cancelled with cancel()");
    }
  }
}