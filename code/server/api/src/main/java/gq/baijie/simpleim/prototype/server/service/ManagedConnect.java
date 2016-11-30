package gq.baijie.simpleim.prototype.server.service;

import gq.baijie.simpleim.prototype.server.service.Server.Connect;

public class ManagedConnect {

  private final Connect connect;
  private AccountHandleServer accountHandleServer;

  ManagedConnect(Connect connect) {
    this.connect = connect;
  }

  void setAccountHandleServer(AccountHandleServer accountHandleServer) {
    this.accountHandleServer = accountHandleServer;
  }

  public AccountHandleServer getAccountHandleServer() {
    return accountHandleServer;
  }
}
