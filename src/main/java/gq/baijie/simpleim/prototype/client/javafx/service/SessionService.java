package gq.baijie.simpleim.prototype.client.javafx.service;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class SessionService {

  boolean haveLoggedIn = false;
  /* when haveLoggedIn = true */
  String accountId;
  String token;//?

  @Inject
  public SessionService() {
  }

}
