package gq.baijie.simpleim.prototype.server.service;

import java.util.List;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import gq.baijie.simpleim.prototype.business.api.AccountService;
import gq.baijie.simpleim.prototype.business.api.AccountService.LoginResult;
import gq.baijie.simpleim.prototype.business.api.AccountService.RegisterResult;
import gq.baijie.simpleim.prototype.server.service.AccountServerHandle.OnReceiveRequestListener;

public class AccountServer {

  private final RequestListener listener = new RequestListener();

  @Inject
  AccountService accountService;

  @Inject
  public AccountServer() {
  }

  public void onReceiveHandler(AccountServerHandle handle) {
    handle.setOnReceiveRequestListener(listener);
  }

  private class RequestListener implements OnReceiveRequestListener {

    @Override
    public RegisterResult onReceiveRegisterRequest(
        @Nonnull String accountId, @Nonnull String password) {
      return accountService.register(accountId, password);
    }

    @Override
    public LoginResult onReceiveLoginRequest(
        @Nonnull String accountId, @Nonnull String password) {
      return accountService.login(accountId, password);
    }

    @Override
    public void onReceiveLogoutRequest(@Nonnull String accountId) {
      accountService.logout(accountId);
    }

    @Override
    public List<String> onReceiveOnlineUsersRequest() {
      return accountService.onlineUsers();
    }

  }

}
