package gq.baijie.simpleim.prototype.server.impl.vertx;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Singleton;

import gq.baijie.simpleim.prototype.business.api.AccountService;
import gq.baijie.simpleim.prototype.server.impl.vertx.codec.AccountServerRequest;
import gq.baijie.simpleim.prototype.server.impl.vertx.codec.AccountServerResponse;
import gq.baijie.simpleim.prototype.server.impl.vertx.codec.Record;
import gq.baijie.simpleim.prototype.server.impl.vertx.codec.RecordCodec;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetClient;
import io.vertx.core.net.NetClientOptions;
import io.vertx.core.net.NetSocket;
import io.vertx.core.parsetools.RecordParser;
import rx.subjects.PublishSubject;

@Singleton
public class RemoteAccountService implements AccountService {

  private final Logger logger = LoggerFactory.getLogger(RemoteAccountService.class);

  @Inject
  RecordCodec recordCodec;

  private final Vertx vertx = Vertx.vertx();
  private final NetClient client;
  private NetSocket socket = null;

  private final PublishSubject<AccountServerResponse> responses = PublishSubject.create();

  @Inject
  public RemoteAccountService() {
    NetClientOptions options = new NetClientOptions()
        .setConnectTimeout(10000);
    client = vertx.createNetClient(options);
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
    if (AccountServerResponse.class.equals(decodeRecord.data.getClass())) {
      responses.onNext((AccountServerResponse) decodeRecord.data);
    } else {
      logger.warn("RemoteAccountService received non-response record: {}", record);
    }
  }

  private void writeRecord(Record record) {
    final Buffer encodedRecord = recordCodec.encodeToRecord(record);
    final Buffer frame = Buffer.buffer()
        .appendInt(encodedRecord.length())
        .appendBuffer(encodedRecord);
    socket.write(frame);
  }

  @Override
  public RegisterResult register(@Nonnull String accountId, @Nonnull String password) {
    Record requestRecord = Record.of(AccountServerRequest.registerRequest(accountId, password));
    writeRecord(requestRecord);
    return responses
        .filter(response -> response.requestId == requestRecord.id)
        .take(1)
        .toSingle()
        .toBlocking()
        .value()
        .toRegisterResult();
  }

  @Override
  public LoginResult login(@Nonnull String accountId, @Nonnull String password) {
    Record requestRecord = Record.of(AccountServerRequest.loginRequest(accountId, password));
    writeRecord(requestRecord);
    return responses
        .filter(response -> response.requestId == requestRecord.id)
        .take(1)
        .toSingle()
        .toBlocking()
        .value()
        .toLoginResult();
  }

  @Override
  public void logout(@Nonnull String accountId) {
    Record requestRecord = Record.of(AccountServerRequest.logoutRequest(accountId));
    writeRecord(requestRecord);
  }

  @Override
  public List<String> onlineUsers() {
    Record requestRecord = Record.of(AccountServerRequest.getOnlineUsersRequest());
    writeRecord(requestRecord);
    return responses
        .filter(response -> response.requestId == requestRecord.id)
        .take(1)
        .toSingle()
        .toBlocking()
        .value()
        .toStringList();
  }
}
