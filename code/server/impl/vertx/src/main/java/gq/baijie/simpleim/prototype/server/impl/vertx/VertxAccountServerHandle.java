package gq.baijie.simpleim.prototype.server.impl.vertx;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import gq.baijie.simpleim.prototype.business.api.AccountService;
import gq.baijie.simpleim.prototype.server.impl.vertx.codec.AccountServerRequest;
import gq.baijie.simpleim.prototype.server.impl.vertx.codec.AccountServerResponse;
import gq.baijie.simpleim.prototype.server.impl.vertx.codec.Record;
import gq.baijie.simpleim.prototype.server.impl.vertx.codec.RecordCodec;
import gq.baijie.simpleim.prototype.server.service.AccountServerHandle;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetSocket;
import io.vertx.core.parsetools.RecordParser;
import rx.Observable;
import rx.subjects.PublishSubject;

public class VertxAccountServerHandle implements AccountServerHandle {

  private final Logger logger = LoggerFactory.getLogger(VertxAccountServerHandle.class);

  private final NetSocket socket;

  private final RecordCodec recordCodec;

  private final PublishSubject<Request> requests = PublishSubject.create();

  public VertxAccountServerHandle(NetSocket socket, RecordCodec recordCodec) {
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
    final Record decodeRecord = recordCodec.decodeRecord(record);
    if (AccountServerRequest.class.equals(decodeRecord.data.getClass())) {
      onReceiveRequest(decodeRecord);
    } else {
      logger.warn("received non-request record: {}", record);
    }
  }

  private void onReceiveRequest(Record<AccountServerRequest> requestRecord) {
    final Object request = requestRecord.data.data;
    Request businessRequest = null;
    if (request == null) {
      businessRequest = new GetOnlineUsersRequest(requestRecord.id);
    } else if (RegisterRequestParameters.class.equals(request.getClass())) {
      businessRequest = new RegisterRequest(requestRecord.id, (RegisterRequestParameters) request);
    } else if (LoginRequestParameters.class.equals(request.getClass())) {
      businessRequest = new LoginRequest(requestRecord.id, (LoginRequestParameters) request);
    } else if (LogoutRequestParameters.class.equals(request.getClass())) {
      businessRequest = new LogoutRequest(requestRecord.id, (LogoutRequestParameters) request);
    }
    if (businessRequest != null) {
      requests.onNext(businessRequest);
    }
  }

  @Override
  public Observable<Request> requests() {
    return requests;
  }

  private class BaseRequest {

    /**
     * 2 byte
     */
    final short requestId;

    public BaseRequest(short requestId) {
      this.requestId = requestId;
    }

    void writeResponse(Record responseRecord) {
      final Buffer record = recordCodec.encodeToRecord(responseRecord);
      final Buffer frame = Buffer.buffer()
          .appendInt(record.length())
          .appendBuffer(record);
      socket.write(frame);
    }

  }

  private class RegisterRequest extends BaseRequest implements AccountServerHandle.RegisterRequest {

    private final RegisterRequestParameters parameters;

    public RegisterRequest(short requestId, RegisterRequestParameters parameters) {
      super(requestId);
      this.parameters = parameters;
    }

    @Override
    public RegisterRequestParameters parameters() {
      return parameters;
    }

    @Override
    public void response(AccountService.RegisterResult result) {
      Record<AccountServerResponse> record = Record.of(AccountServerResponse.of(requestId, result));
      writeResponse(record);
    }
  }

  private class LoginRequest extends BaseRequest implements AccountServerHandle.LoginRequest {

    private final LoginRequestParameters parameters;

    public LoginRequest(short requestId, LoginRequestParameters parameters) {
      super(requestId);
      this.parameters = parameters;
    }

    @Override
    public LoginRequestParameters parameters() {
      return parameters;
    }

    @Override
    public void response(AccountService.LoginResult result) {
      Record<AccountServerResponse> record = Record.of(AccountServerResponse.of(requestId, result));
      writeResponse(record);
    }
  }

  private class LogoutRequest extends BaseRequest implements AccountServerHandle.LogoutRequest {

    private final LogoutRequestParameters parameters;

    public LogoutRequest(short requestId, LogoutRequestParameters parameters) {
      super(requestId);
      this.parameters = parameters;
    }

    @Override
    public LogoutRequestParameters parameters() {
      return parameters;
    }

    @Override
    public void response(Void result) {
      Record<AccountServerResponse> record = Record.of(AccountServerResponse.of(requestId, result));
      writeResponse(record);
    }
  }

  private class GetOnlineUsersRequest extends BaseRequest
      implements AccountServerHandle.GetOnlineUsersRequest {

    public GetOnlineUsersRequest(short requestId) {
      super(requestId);
    }

    @Override
    public Void parameters() {
      return null;
    }

    @Override
    public void response(List<String> result) {
      Record<AccountServerResponse> record = Record.of(AccountServerResponse.of(requestId, result));
      writeResponse(record);
    }
  }

}
