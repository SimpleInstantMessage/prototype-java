package gq.baijie.simpleim.prototype.business.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;

import gq.baijie.simpleim.prototype.business.common.AccountService.LoginResult;
import rx.Observable;
import rx.subjects.BehaviorSubject;

public abstract class AbstractAccountService implements AccountService {

  private final Logger logger = LoggerFactory.getLogger(AbstractAccountService.class);

  private final BehaviorSubject<LoginState> loginStateEventBus = BehaviorSubject.create();

  private String loggedInAccountId = null;

  protected void changeLoginState(LoginState state) {
    loginStateEventBus.onNext(state);
  }

  @Override
  public final LoginResult login(@Nonnull String accountId, @Nonnull String password) {
    if (haveLoggedIn()) {
      logger.error("have logged in", new IllegalStateException());
      return null;
    }
    final LoginResult loginResult = doLogin(accountId, password);
    if (loginResult == LoginResult.SUCCESS) {
      loggedInAccountId = accountId;
      changeLoginState(LoginState.LOGGED_IN);
    }
    return loginResult;
  }

  protected abstract LoginResult doLogin(@Nonnull String accountId, @Nonnull String password);

  @Override
  public final void logout() {
    if (!haveLoggedIn()) {
      logger.error("haven't logged in", new IllegalStateException());
      return;
    }
    changeLoginState(LoginState.LOGGED_OUT);
    final String loggedOutAccountId = loggedInAccountId;
    loggedInAccountId = null;
    doLogout(loggedOutAccountId);
  }

  protected abstract void doLogout(@Nonnull String accountId);

  @Override
  public boolean haveLoggedIn() {
    return getLoginState() == LoginState.LOGGED_IN;
  }

  @Override
  public LoginState getLoginState() {
    return loginStateEventBus.getValue();
  }

  @Override
  public Observable<LoginState> loginStateEventBus() {
    return loginStateEventBus;
  }

  @Override
  public String getLoggedInAccountId() {
    return loggedInAccountId;
  }
}
