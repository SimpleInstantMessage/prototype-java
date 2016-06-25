package gq.baijie.simpleim.prototype.client.javafx.service;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Singleton;

import static gq.baijie.simpleim.prototype.client.javafx.service.DummyAccounts.ACCOUNTS;

@Singleton
public class AccountService {

  @Inject
  SessionService sessionService;

  @Inject
  public AccountService() {
  }

  public LoginResult login(@Nonnull String accountId, @Nonnull String password) {
    if (sessionService.haveLoggedIn) {
      throw new IllegalStateException("have logged in");
    }
    if (!ACCOUNTS.containsKey(accountId)) {
      return LoginResult.NO_ACCOUNT;
    }
    if (ACCOUNTS.get(accountId).password.equals(password)) {
      return LoginResult.SUCCESS;
    } else {
      return LoginResult.PASSWORD_ERROR;
    }
  }

  public void logout() {
  }

  public enum LoginResult {
    SUCCESS,
    NO_ACCOUNT,
    PASSWORD_ERROR
  }

}
