package gq.baijie.simpleim.prototype.business.api;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class MemoryAccountService implements AccountService {

  static final Map<String, Account> ACCOUNTS;

  private static final Account[] ACCOUNTS_ARRAY = new Account[]{
      new Account("baijie", "baijie"),
      new Account("admin", "admin")
  };

  static {
    ACCOUNTS = new HashMap<>(2);
    for (Account account : ACCOUNTS_ARRAY) {
      ACCOUNTS.put(account.id, account);
    }
  }

  @Inject
  public MemoryAccountService() {
  }

  @Override
  public RegisterResult register(@Nonnull String accountId, @Nonnull String password) {
    if (ACCOUNTS.containsKey(accountId)) {
      return RegisterResult.DUPLICATED_ACCOUNT_ID;
    }
    ACCOUNTS.put(accountId, new Account(accountId, password));
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

  private static class Account {

    final String id;
    final String password;

    public Account(String id, String password) {
      this.id = id;
      this.password = password;
    }
  }

}
