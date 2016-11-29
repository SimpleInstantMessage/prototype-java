package gq.baijie.simpleim.prototype.server.impl.vertx;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import gq.baijie.simpleim.prototype.business.api.AccountService.LoginResult;
import gq.baijie.simpleim.prototype.business.api.AccountService.RegisterResult;
import gq.baijie.simpleim.prototype.server.impl.vertx.codec.AccountServerRequest;
import gq.baijie.simpleim.prototype.server.impl.vertx.codec.AccountServerRequest.LoginRequestParameters;
import gq.baijie.simpleim.prototype.server.impl.vertx.codec.AccountServerRequest.LogoutRequestParameters;
import gq.baijie.simpleim.prototype.server.impl.vertx.codec.AccountServerRequest.RegisterRequestParameters;
import gq.baijie.simpleim.prototype.server.impl.vertx.codec.AccountServerResponse;
import gq.baijie.simpleim.prototype.server.impl.vertx.codec.Record;
import gq.baijie.simpleim.prototype.server.impl.vertx.codec.RecordCodec;
import gq.baijie.simpleim.prototype.server.service.AccountServerHandle2;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetSocket;
import io.vertx.core.parsetools.RecordParser;

public class VertxAccountServerHandle2 implements AccountServerHandle2 {

  private final Logger logger = LoggerFactory.getLogger(VertxAccountServerHandle2.class);

  private final NetSocket socket;

  private final RecordCodec recordCodec;

  OnReceiveRequestListener requestListener = null;

  public VertxAccountServerHandle2(NetSocket socket, RecordCodec recordCodec) {
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
    final short requestId = requestRecord.id;
    final Object request = requestRecord.data.data;
    if (request == null) {
      onReceiveOnlineUsersRequest(requestId);
    } else if (RegisterRequestParameters.class.equals(request.getClass())) {
      onReceiveRegisterRequest(requestId, (RegisterRequestParameters) request);
    } else if (LoginRequestParameters.class.equals(request.getClass())) {
      onReceiveLoginRequest(requestId, (LoginRequestParameters) request);
    } else if (LogoutRequestParameters.class.equals(request.getClass())) {
      onReceiveLogoutRequest(requestId, (LogoutRequestParameters) request);
    }
  }

  private void onReceiveRegisterRequest(short requestId, RegisterRequestParameters parameters) {
    if (requestListener == null) {
      logger.warn("ignore request because no requestListener");
      return;
    }
    final RegisterResult result =
        requestListener.onReceiveRegisterRequest(parameters.accountId, parameters.password);
    final Record responseRecord = Record.of(AccountServerResponse.of(requestId, result));
    writeResponse(responseRecord);
  }
  private void onReceiveLoginRequest(short requestId, LoginRequestParameters parameters) {
    if (requestListener == null) {
      logger.warn("ignore request because no requestListener");
      return;
    }
    final LoginResult result =
        requestListener.onReceiveLoginRequest(parameters.accountId, parameters.password);
    final Record responseRecord = Record.of(AccountServerResponse.of(requestId, result));
    writeResponse(responseRecord);
  }
  private void onReceiveLogoutRequest(short requestId, LogoutRequestParameters parameters) {
    if (requestListener == null) {
      logger.warn("ignore request because no requestListener");
      return;
    }
    requestListener.onReceiveLogoutRequest(parameters.accountId);
    final Record responseRecord = Record.of(AccountServerResponse.of(requestId, (Void) null));
    writeResponse(responseRecord);
  }
  private void onReceiveOnlineUsersRequest(short requestId) {
    if (requestListener == null) {
      logger.warn("ignore request because no requestListener");
      return;
    }
    final List<String> result = requestListener.onReceiveOnlineUsersRequest();
    final Record responseRecord = Record.of(AccountServerResponse.of(requestId, result));
    writeResponse(responseRecord);
  }

  private void writeResponse(Record responseRecord) {
    final Buffer record = recordCodec.encodeToRecord(responseRecord);
    final Buffer frame = Buffer.buffer()
        .appendInt(record.length())
        .appendBuffer(record);
    socket.write(frame);
  }

  @Override
  public void setOnReceiveRequestListener(OnReceiveRequestListener listener) {
    requestListener = listener;
  }

}
