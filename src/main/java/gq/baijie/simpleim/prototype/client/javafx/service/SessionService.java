package gq.baijie.simpleim.prototype.client.javafx.service;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class SessionService {

  State state = State.LOGGED_OUT;
  /* when state == LOGGED_IN */
  String accountId;
  String token;//TODO use this?

  @Inject
  public SessionService() {
  }

  public void gotoHaveLoggedInState(@Nonnull String accountId) {//TODO token?
    this.accountId = accountId;
    state = State.LOGGED_IN;
  }

  public void gotoHaveLoggedOutState() {
    state = State.LOGGED_OUT;
    accountId = null;
  }

  public boolean haveLoggedIn() {
    return state == State.LOGGED_IN;
  }

  public enum State {
    LOGGED_IN,
    LOGGED_OUT
  }

}
