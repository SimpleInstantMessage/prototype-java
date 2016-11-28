package gq.baijie.simpleim.prototype.server.service;

import javax.inject.Inject;

import gq.baijie.simpleim.prototype.business.api.AccountService;
import gq.baijie.simpleim.prototype.server.service.AccountServerHandle.GetOnlineUsersRequest;
import gq.baijie.simpleim.prototype.server.service.AccountServerHandle.LoginRequest;
import gq.baijie.simpleim.prototype.server.service.AccountServerHandle.LogoutRequest;
import gq.baijie.simpleim.prototype.server.service.AccountServerHandle.RegisterRequest;
import rx.observables.ConnectableObservable;

public class AccountServer {

  @Inject
  AccountService accountService;

  @Inject
  public AccountServer() {
  }

  public void onReceiveHandler(AccountServerHandle handle) {
    final ConnectableObservable<AccountServerHandle.Request> requests = handle.requests().publish();

    requests.ofType(RegisterRequest.class).subscribe(request -> request.response(
        accountService.register(request.parameters().accountId, request.parameters().password)
    ));
    requests.ofType(LoginRequest.class).subscribe(request -> request.response(
        accountService.login(request.parameters().accountId, request.parameters().password)
    ));
    requests.ofType(LogoutRequest.class).subscribe(request -> {
      accountService.logout(request.parameters().accountId);
      request.response(null);
    });
    requests.ofType(GetOnlineUsersRequest.class).subscribe(request -> request.response(
        accountService.onlineUsers()
    ));

    requests.connect();
  }

}
