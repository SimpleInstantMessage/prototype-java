package gq.baijie.simpleim.prototype.business.api;

import java.util.Arrays;
import java.util.List;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Singleton;

import static gq.baijie.simpleim.prototype.business.api.DummyAccounts.ACCOUNTS;

@Singleton
public class AccountService {

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
    if (!ACCOUNTS.containsKey(accountId)) {
      return LoginResult.NO_ACCOUNT;
    }
    if (ACCOUNTS.get(accountId).password.equals(password)) {
      return LoginResult.SUCCESS;
    } else {
      return LoginResult.PASSWORD_ERROR;
    }
  }

  public void logout(@Nonnull String accountId) {
  }

  /**
   * get list of account id who are online now
   */
  public List<String> onlineUsers() {
    return Arrays.asList("baijie", "admin");
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
