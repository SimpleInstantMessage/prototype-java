package gq.baijie.simpleim.prototype.business.api;

import java.util.Arrays;
import java.util.List;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Singleton;

import static gq.baijie.simpleim.prototype.business.api.DummyAccounts.ACCOUNTS;

@Singleton
public class MemoryAccountService implements AccountService {

  @Inject
  public MemoryAccountService() {
  }

  @Override
  public RegisterResult register(@Nonnull String accountId, @Nonnull String password) {
    if (ACCOUNTS.containsKey(accountId)) {
      return RegisterResult.DUPLICATED_ACCOUNT_ID;
    }
    ACCOUNTS.put(accountId, new DummyAccounts.Account(accountId, password));
    return RegisterResult.SUCCESS;
  }

  @Override
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

  @Override
  public void logout(@Nonnull String accountId) {
  }

  @Override
  public List<String> onlineUsers() {
    return Arrays.asList("baijie", "admin");
  }

}
