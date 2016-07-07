package gq.baijie.simpleim.prototype.io.network.netty.server.service;

import java.util.HashMap;
import java.util.Map;

public class DummyAccounts {

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

  public static class Account {

    final String id;
    final String password;

    public Account(String id, String password) {
      this.id = id;
      this.password = password;
    }
  }

}
