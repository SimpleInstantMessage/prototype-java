package gq.baijie.simpleim.prototype.server.impl.vertx;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gq.baijie.simpleim.prototype.business.api.Message;
import gq.baijie.simpleim.prototype.business.api.MessageSwitchService;
import gq.baijie.simpleim.prototype.server.impl.vertx.codec.Record;
import gq.baijie.simpleim.prototype.server.impl.vertx.codec.RecordCodec;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetSocket;
import io.vertx.core.parsetools.RecordParser;
import rx.Observable;
import rx.subjects.PublishSubject;

class VertxSession implements MessageSwitchService.Session {

  private final Logger logger = LoggerFactory.getLogger(VertxSession.class);

  private final PublishSubject<Message> receiveMessages = PublishSubject.create();

  private final NetSocket socket;

  private final RecordCodec recordCodec;

  VertxSession(NetSocket socket, RecordCodec recordCodec) {
    this.socket = socket;
    this.recordCodec = recordCodec;
    initSocketHandler();
  }

  private void initSocketHandler() {
    final int lengthChunkSize = 4;
    RecordParser parser = RecordParser.newFixed(lengthChunkSize, null);
    Handler<Buffer> handler = new Handler<Buffer>() {
      boolean isSizeChunk = true;

      @Override
      public void handle(Buffer buffer) {
        if (isSizeChunk) {
          int size = buffer.getInt(0);
          parser.fixedSizeMode(size);
          isSizeChunk = false;
        } else {
          onReceiveRecord(buffer);
          parser.fixedSizeMode(lengthChunkSize);
          isSizeChunk = true;
        }
      }
    };
    parser.setOutput(handler);
    socket.handler(parser);
  }

  private void onReceiveRecord(Buffer record) {
    onReceiveRecord(recordCodec.decodeRecord(record));
  }

  private void onReceiveRecord(Record record) {
    if (Message.class.equals(record.data.getClass())) {
      onReceiveMessage(((Message) record.data));
    } else {
      logger.warn("unknown type of received record.data: {}", record.data.getClass());
    }
  }

  private void onReceiveMessage(Message message) {
    receiveMessages.onNext(message);
  }

  private void writeRecord(Record record) {
    final Buffer encodedRecord = recordCodec.encodeToRecord(record);
    final Buffer frame = Buffer.buffer()
        .appendInt(encodedRecord.length())
        .appendBuffer(encodedRecord);
    socket.write(frame);
  }

  @Override
  public void sendMessage(Message message) {
    writeRecord(Record.of(message));
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
