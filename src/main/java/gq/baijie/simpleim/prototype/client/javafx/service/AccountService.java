package gq.baijie.simpleim.prototype.client.javafx.service;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class AccountService {

  @Inject
  SessionService sessionService;

  @Inject
  public AccountService() {
  }

  public void login(String accountId, String password) {
    System.out.println("handleLogin()");
    System.out.println("accountId:" + accountId);
    System.out.println("password:" + password);
  }

  public void logout() {
  }

}
