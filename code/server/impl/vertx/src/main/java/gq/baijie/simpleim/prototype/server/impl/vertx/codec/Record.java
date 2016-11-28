package gq.baijie.simpleim.prototype.server.impl.vertx.codec;

import java.util.concurrent.atomic.AtomicInteger;

public class Record<T> {

  private static AtomicInteger idGenerator = new AtomicInteger();

  public short id;

  public byte type;

  public T data;

  public Record() {
  }

  private Record(byte type, T data) {
//    this.id = id;
    this.id = (short) idGenerator.incrementAndGet();
    this.type = type;
    this.data = data;
  }

  /*public static <T> Record of(short id, byte type, T data) {
    Record<T> result = new Record<>();
    result.id = id;
    result.type = type;
    result.data = data;
    return result;
  }*/
  public static Record<AccountServerRequest> of(AccountServerRequest request) {
    return new Record<>((byte) 1, request);
  }

  public static Record<AccountServerResponse> of(AccountServerResponse response) {
    return new Record<>((byte) 2, response);
  }

}
