package gq.baijie.simpleim.prototype.impl.vertx.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Singleton;

import gq.baijie.simpleim.prototype.business.common.ApplicationService;
import gq.baijie.simpleim.prototype.impl.vertx.codec.Record;
import gq.baijie.simpleim.prototype.impl.vertx.codec.RecordCodec;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetClient;
import io.vertx.core.net.NetClientOptions;
import io.vertx.core.net.NetSocket;
import io.vertx.core.parsetools.RecordParser;
import rx.Observable;
import rx.subjects.PublishSubject;

@Singleton
public class RemoteChannelService {

  private final Logger logger = LoggerFactory.getLogger(RemoteChannelService.class);

  private final ApplicationService applicationService;

  @Inject
  RecordCodec recordCodec;

  private final Vertx vertx = Vertx.vertx();
  private final NetClient client =
      vertx.createNetClient(new NetClientOptions().setConnectTimeout(10000));

  private NetSocket socket = null;

  private final PublishSubject<Record> records = PublishSubject.create();

  @Inject
  public RemoteChannelService(ApplicationService applicationService) {
    this.applicationService = applicationService;
    connect();
    listenCloseEvent();
  }

  private void listenCloseEvent() {
    applicationService.closeEventBus().subscribe(e -> {
      vertx.close();
    });
  }

  private void connect() {
    client.connect(4321, "localhost", res -> {
      if (res.succeeded()) {
        logger.info("RemoteAccountService Connected!");
        socket = res.result();
        initSocketHandler();
      } else {
        logger.warn("RemoteAccountService Failed to connect", res.cause());
      }
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
    if (socket != null) {
      socket.write(frame);
    } else {
      logger.warn("because socket == null, write this record to /dev/null: {}", record);
    }
  }

}
