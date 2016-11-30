package gq.baijie.simpleim.prototype.impl.vertx;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

import gq.baijie.simpleim.prototype.impl.vertx.codec.Record;
import gq.baijie.simpleim.prototype.impl.vertx.codec.RecordCodec;
import gq.baijie.simpleim.prototype.business.server.Server.Connect;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetSocket;
import io.vertx.core.parsetools.RecordParser;
import rx.Observable;
import rx.subjects.PublishSubject;

class NetSocketConnect implements Connect {

  final NetSocket socket;

  private final List<Object> handles = new LinkedList<>();

  private final RecordCodec recordCodec;

  private Consumer<Connect> closeListener;

  private final PublishSubject<Record> records = PublishSubject.create();

  NetSocketConnect(NetSocket socket, RecordCodec recordCodec) {
    this.socket = socket;
    this.recordCodec = recordCodec;
    initSocketCloseHandler();
    initSocketHandler();
  }

  private void initSocketCloseHandler() {
    socket.closeHandler(event -> {
      if (closeListener != null) {
        closeListener.accept(this);
      }
      //TODO socket.close();
    });
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
    final Record decodeRecord = recordCodec.decodeRecord(record);
    onReceiveRecord(decodeRecord);
  }

  private void onReceiveRecord(Record record) {
    records.onNext(record);
  }

  Observable<Record> records() {
    return records;
  }

  void writeRecord(final Record record) {
    final Buffer encodedRecord = recordCodec.encodeToRecord(record);
    final Buffer frame = Buffer.buffer()
        .appendInt(encodedRecord.length())
        .appendBuffer(encodedRecord);
    socket.write(frame);
  }

  void addHandle(Object handle) {
    handles.add(handle);
  }

  @Override
  public Observable<Object> handles() {
    return Observable.from(handles);
  }

  @Override
  public void setOnCloseListener(Consumer<Connect> listener) {
    closeListener = listener;
  }

}
