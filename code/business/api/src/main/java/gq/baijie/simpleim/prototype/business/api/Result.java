package gq.baijie.simpleim.prototype.business.api;

public class Result<T, E> {

  private final T result;

  private final E error;

  public static <T, E> Result<T, E> succeed(T result) {
    return new Result<>(result, null);
  }

  public static <T, E> Result<T, E> error(E error) {
    return new Result<>(null, error);
  }

  private Result(T result, E error) {
    this.result = result;
    this.error = error;
  }

  public boolean succeeded() {
    return error == null;
  }

  public T result() {
    return result;
  }

  public E error() {
    return error;
  }

}
