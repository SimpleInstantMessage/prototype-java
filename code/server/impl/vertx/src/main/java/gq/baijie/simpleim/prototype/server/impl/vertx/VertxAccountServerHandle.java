package gq.baijie.simpleim.prototype.server.impl.vertx;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import javax.annotation.Nullable;

import gq.baijie.simpleim.prototype.business.api.AccountService.LoginResult;
import gq.baijie.simpleim.prototype.business.api.AccountService.RegisterResult;
import gq.baijie.simpleim.prototype.server.impl.vertx.codec.AccountServerRequest;
import gq.baijie.simpleim.prototype.server.impl.vertx.codec.AccountServerRequest.LoginRequestParameters;
import gq.baijie.simpleim.prototype.server.impl.vertx.codec.AccountServerRequest.LogoutRequestParameters;
import gq.baijie.simpleim.prototype.server.impl.vertx.codec.AccountServerRequest.RegisterRequestParameters;
import gq.baijie.simpleim.prototype.server.impl.vertx.codec.AccountServerResponse;
import gq.baijie.simpleim.prototype.server.impl.vertx.codec.Record;
import gq.baijie.simpleim.prototype.server.service.AccountServerHandle;

public class VertxAccountServerHandle implements AccountServerHandle {

  private final Logger logger = LoggerFactory.getLogger(VertxAccountServerHandle.class);

  private final NetSocketConnect connect;

  private String loggedInAccountId = null;

  private OnReceiveRequestListener requestListener = null;

  public VertxAccountServerHandle(NetSocketConnect connect) {
    this.connect = connect;
    init();
  }

  private void init() {
    connect.records()
        .filter(record -> AccountServerRequest.class.equals(record.data.getClass()))
        .subscribe(record -> onReceiveRequest(record));
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
    if (result == LoginResult.SUCCESS) {
      loggedInAccountId = parameters.accountId;
    }
    final Record responseRecord = Record.of(AccountServerResponse.of(requestId, result));
    writeResponse(responseRecord);
  }
  private void onReceiveLogoutRequest(short requestId, LogoutRequestParameters parameters) {
    if (requestListener == null) {
      logger.warn("ignore request because no requestListener");
      return;
    }
    requestListener.onReceiveLogoutRequest(parameters.accountId);
    loggedInAccountId = null;
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
    connect.writeRecord(responseRecord);
  }

  @Override
  public void setOnReceiveRequestListener(OnReceiveRequestListener listener) {
    requestListener = listener;
  }

  @Nullable
  String getLoggedInAccountId() {
    return loggedInAccountId;
  }

}
