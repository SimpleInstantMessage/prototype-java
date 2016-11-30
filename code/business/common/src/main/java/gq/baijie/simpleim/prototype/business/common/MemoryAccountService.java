package gq.baijie.simpleim.prototype.business.common;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class MemoryAccountService implements AccountService {

  private static final Map<String, Account> ACCOUNTS;

  private static final Account[] DEFAULT_ACCOUNTS = new Account[]{
      new Account("baijie", "baijie"),
      new Account("admin", "admin")
  };

  static {
    ACCOUNTS = new HashMap<>();
    for (Account account : DEFAULT_ACCOUNTS) {
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
    final Account account = ACCOUNTS.get(accountId);
    if (account == null) {
      return LoginResult.NO_ACCOUNT;
    }
    if (!account.password.equals(password)) {
      return LoginResult.PASSWORD_ERROR;
    }
    if (account.isLoggedIn) {
      return LoginResult.HAVE_LOGGED_IN;
    }
    account.isLoggedIn = true;
    return LoginResult.SUCCESS;
  }

  @Override
  public void logout(@Nonnull String accountId) {
    final Account account = ACCOUNTS.get(accountId);
    if (account != null) {
      account.isLoggedIn = false;
    }
  }

  @Override
  public List<String> onlineUsers() {
    return ACCOUNTS.values().stream()
        .filter(account -> account.isLoggedIn)
        .map(account -> account.id)
        .collect(Collectors.toList());
  }

  private static class Account {

    final String id;
    final String password;
    boolean isLoggedIn = false;

    public Account(String id, String password) {
      this.id = id;
      this.password = password;
    }
  }

}
