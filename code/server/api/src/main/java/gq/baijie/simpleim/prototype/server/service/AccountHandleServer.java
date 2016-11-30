package gq.baijie.simpleim.prototype.server.service;

import java.util.List;

import javax.annotation.Nonnull;

import gq.baijie.simpleim.prototype.business.api.AccountService;
import gq.baijie.simpleim.prototype.business.api.AccountService.LoginResult;
import gq.baijie.simpleim.prototype.business.api.AccountService.RegisterResult;
import gq.baijie.simpleim.prototype.server.service.AccountServerHandle.OnReceiveRequestListener;

public class AccountHandleServer {
  private final AccountService accountService;

  private String loggedInAccountId;

  public AccountHandleServer(AccountService accountService, AccountServerHandle handle) {
    this.accountService = accountService;
    init(handle);
  }

  private void init(AccountServerHandle handle) {
    handle.setOnReceiveRequestListener(new RequestListener());
  }

  private void onLoggedIn(@Nonnull String accountId) {
    loggedInAccountId = accountId;
  }

  private void onLoggedOut() {
    loggedInAccountId = null;
  }

  String getLoggedInAccountId() {
    return loggedInAccountId;
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
      final LoginResult loginResult = accountService.login(accountId, password);
      if (loginResult == LoginResult.SUCCESS) {
        onLoggedIn(accountId);
      }
      return loginResult;
    }

    @Override
    public void onReceiveLogoutRequest(@Nonnull String accountId) {
      accountService.logout(accountId);
      onLoggedOut();
    }

    @Override
    public List<String> onReceiveOnlineUsersRequest() {
      return accountService.onlineUsers();
    }

  }

}
