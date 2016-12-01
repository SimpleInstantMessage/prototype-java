package gq.baijie.simpleim.prototype.business.server;

import java.util.List;

import javax.annotation.Nonnull;

import gq.baijie.simpleim.prototype.business.common.AccountService.LoginResult;
import gq.baijie.simpleim.prototype.business.common.AccountService.RegisterResult;

public interface AccountSession extends Server.Connect.Session {

  void setOnReceiveRequestListener(OnReceiveRequestListener listener);

  interface OnReceiveRequestListener {
    RegisterResult onReceiveRegisterRequest(@Nonnull String accountId, @Nonnull String password);
    LoginResult onReceiveLoginRequest(@Nonnull String accountId, @Nonnull String password);
    void onReceiveLogoutRequest(@Nonnull String accountId);
    List<String> onReceiveOnlineUsersRequest();
  }

}
