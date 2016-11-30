package gq.baijie.simpleim.prototype.server.service;

import javax.inject.Inject;

import gq.baijie.simpleim.prototype.business.api.AccountService;

public class AccountServer {

  @Inject
  AccountService accountService;

  @Inject
  public AccountServer() {
  }

  public AccountHandleServer onReceiveHandle(AccountServerHandle handle) {
    return new AccountHandleServer(accountService, handle);
  }

}
