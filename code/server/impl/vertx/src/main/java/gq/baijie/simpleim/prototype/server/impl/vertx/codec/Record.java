package gq.baijie.simpleim.prototype.server.impl.vertx.codec;

import java.util.concurrent.atomic.AtomicInteger;

import gq.baijie.simpleim.prototype.business.api.Message;

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

  public static Record<AccountServerRequest> of(AccountServerRequest request) {
    return new Record<>((byte) 1, request);
  }

  public static Record<AccountServerResponse> of(AccountServerResponse response) {
    return new Record<>((byte) 2, response);
  }

  public static Record<Message> of(Message message) {
    return new Record<>(MessageCodec.RECORD_TYPE, message);
  }

}
