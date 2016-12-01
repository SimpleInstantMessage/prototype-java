package gq.baijie.simpleim.prototype.business.server.session;

import java.util.List;

import javax.annotation.Nonnull;

import gq.baijie.simpleim.prototype.business.common.AccountService;
import gq.baijie.simpleim.prototype.business.common.AccountService.LoginResult;
import gq.baijie.simpleim.prototype.business.common.AccountService.RegisterResult;
import gq.baijie.simpleim.prototype.business.server.ManagedConnect;
import gq.baijie.simpleim.prototype.business.server.SessionHandler;
import gq.baijie.simpleim.prototype.business.server.session.AccountSession.OnReceiveRequestListener;

public class AccountSessionHandler implements SessionHandler {
  private final AccountService accountService;

  private String loggedInAccountId;

  public AccountSessionHandler(AccountService accountService, AccountSession session) {
    this.accountService = accountService;
    init(session);
  }

  @Override
  public void bindConnect(ManagedConnect connect) {
    connect.getCloseEvents().subscribe(c -> {
      if (loggedInAccountId != null) {
        accountService.logout(loggedInAccountId);
        onLoggedOut();
      }
    });
  }

  private void init(AccountSession handle) {
    handle.setOnReceiveRequestListener(new RequestListener());
  }

  private void onLoggedIn(@Nonnull String accountId) {
    if (loggedInAccountId != null) {
      accountService.logout(loggedInAccountId);
      onLoggedOut();
    }
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
