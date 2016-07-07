package gq.baijie.simpleim.prototype.io.network.netty.server.service;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Singleton;

import static gq.baijie.simpleim.prototype.io.network.netty.server.service.DummyAccounts.ACCOUNTS;

@Singleton
public class AccountService {

  @Inject
  SessionService sessionService;

  @Inject
  public AccountService() {
  }

  public RegisterResult register(@Nonnull String accountId, @Nonnull String password) {
    if (ACCOUNTS.containsKey(accountId)) {
      return RegisterResult.DUPLICATED_ACCOUNT_ID;
    }
    ACCOUNTS.put(accountId, new DummyAccounts.Account(accountId, password));
    return RegisterResult.SUCCESS;
  }

  public LoginResult login(@Nonnull String accountId, @Nonnull String password) {
    if (sessionService.haveLoggedIn()) {
      throw new IllegalStateException("have logged in");
    }
    if (!ACCOUNTS.containsKey(accountId)) {
      return LoginResult.NO_ACCOUNT;
    }
    if (ACCOUNTS.get(accountId).password.equals(password)) {
      sessionService.gotoHaveLoggedInState(accountId);
      return LoginResult.SUCCESS;
    } else {
      return LoginResult.PASSWORD_ERROR;
    }
  }

  public void logout() {
    sessionService.gotoHaveLoggedOutState();
  }

  public enum RegisterResult {
    SUCCESS,
    DUPLICATED_ACCOUNT_ID
  }

  public enum LoginResult {
    SUCCESS,
    NO_ACCOUNT,
    PASSWORD_ERROR
  }

}
