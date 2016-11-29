package gq.baijie.simpleim.prototype.server.impl.vertx;

import gq.baijie.simpleim.prototype.business.api.Message;
import gq.baijie.simpleim.prototype.business.api.MessageSwitchService;
import gq.baijie.simpleim.prototype.server.impl.vertx.codec.MessageRecordCodec;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetSocket;
import io.vertx.core.parsetools.RecordParser;
import rx.Observable;
import rx.subjects.PublishSubject;

class VertxSession implements MessageSwitchService.Session {
  // UTF-8 doesn't contain RECORD_DELIMITER
  private static final Buffer RECORD_DELIMITER = Buffer.buffer(1).appendByte((byte) 0b1100_0001);

  private final PublishSubject<Message> receiveMessages = PublishSubject.create();

  private final NetSocket socket;

  VertxSession(NetSocket socket) {
    this.socket = socket;
    socket.handler(RecordParser.newDelimited(RECORD_DELIMITER, this::onReceiveRecord));
  }

  private void onReceiveRecord(Buffer record) {
    onReceiveMessage(MessageRecordCodec.decodeMessage(record));
  }

  private void onReceiveMessage(Message message) {
    receiveMessages.onNext(message);
  }

  @Override
  public void sendMessage(Message message) {
    final Buffer buffer = MessageRecordCodec.encodeMessage(message);
    buffer.appendBuffer(RECORD_DELIMITER);
    socket.write(buffer);
  }

  @Override
  public Observable<Message> receiveMessages() {
    return receiveMessages;
  }

  @Override
  public void close() {
    socket.close();
  }

}
