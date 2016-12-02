package gq.baijie.simpleim.prototype.impl.vertx.client;

import java.util.List;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Singleton;

import gq.baijie.simpleim.prototype.business.client.AbstractAccountService;
import gq.baijie.simpleim.prototype.business.common.AccountService.LoginResult;
import gq.baijie.simpleim.prototype.business.common.AccountService.RegisterResult;
import gq.baijie.simpleim.prototype.impl.vertx.codec.AccountServerRequest;
import gq.baijie.simpleim.prototype.impl.vertx.codec.AccountServerResponse;
import gq.baijie.simpleim.prototype.impl.vertx.codec.Record;
import rx.subjects.PublishSubject;

@Singleton
public class RemoteAccountService extends AbstractAccountService {

  private final RemoteChannelService channelService;

  private final PublishSubject<AccountServerResponse> responses = PublishSubject.create();

  @Inject
  public RemoteAccountService(RemoteChannelService channelService) {
    this.channelService = channelService;
    bindChannelService();
  }

  private void bindChannelService() {
    // receive AccountServerResponse records
    channelService.records()
        .map(record -> record.data)
        .ofType(AccountServerResponse.class)
        .subscribe(responses);
    // to logged out state when lose connection
    channelService.getConnectStateEventBus()
        .filter(connectState -> connectState == RemoteChannelService.ConnectState.OFF_LINE)
        .filter(connectState -> haveLoggedIn())
        .subscribe(connectState -> changeLoginState(LoginState.LOGGED_OUT));
  }

  private void writeRecord(Record record) {
    channelService.writeRecord(record);
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
  protected LoginResult doLogin(@Nonnull String accountId, @Nonnull String password) {
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
  protected void doLogout(@Nonnull String accountId) {
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
