package gq.baijie.simpleim.prototype.server.service;

import java.util.List;

import javax.annotation.Nonnull;

import gq.baijie.simpleim.prototype.business.api.AccountService.LoginResult;
import gq.baijie.simpleim.prototype.business.api.AccountService.RegisterResult;

public interface AccountServerHandle2 {

  void setOnReceiveRequestListener(OnReceiveRequestListener listener);

  interface OnReceiveRequestListener {
    RegisterResult onReceiveRegisterRequest(@Nonnull String accountId, @Nonnull String password);
    LoginResult onReceiveLoginRequest(@Nonnull String accountId, @Nonnull String password);
    void onReceiveLogoutRequest(@Nonnull String accountId);
    List<String> onReceiveOnlineUsersRequest();
  }

}
