package gq.baijie.simpleim.prototype.business.client;

import java.util.List;

import javax.annotation.Nonnull;

import gq.baijie.simpleim.prototype.business.common.AccountService.LoginResult;
import gq.baijie.simpleim.prototype.business.common.AccountService.RegisterResult;
import rx.Observable;

public interface AccountService {

  RegisterResult register(@Nonnull String accountId, @Nonnull String password);

  LoginResult login(@Nonnull String accountId, @Nonnull String password);

  void logout();

  default boolean haveLoggedIn() {
    return getLoginState() == LoginState.LOGGED_IN;
  }

  LoginState getLoginState();

  Observable<LoginState> loginStateEventBus();

  String getLoggedInAccountId();

  /**
   * get list of account id who are online now
   */
  List<String> onlineUsers();

  enum LoginState {
    LOGGED_IN,
    LOGGED_OUT
  }

}
