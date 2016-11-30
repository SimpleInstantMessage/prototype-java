package gq.baijie.simpleim.prototype.server.service;

import java.util.LinkedList;
import java.util.List;

import javax.inject.Inject;

import gq.baijie.simpleim.prototype.business.api.AccountService;
import gq.baijie.simpleim.prototype.business.api.MessageSwitchService;
import gq.baijie.simpleim.prototype.server.service.Server.Connect;

public class ConnectServer {

  @Inject
  MessageSwitchService messageSwitchService;
  @Inject
  AccountService accountService;

  private final List<ManagedConnect> connects = new LinkedList<>();

  @Inject
  public ConnectServer() {
  }

  public void onNewConnect(Connect connect) {
    final ManagedConnect managedConnect = new ManagedConnect(connect);
    connects.add(managedConnect);
    connect.setOnCloseListener(c -> connects.remove(managedConnect));
    connect.handles().subscribe(handle -> onNewHandle(managedConnect, handle));
  }

  private void onNewHandle(ManagedConnect connect, Object handle) {
    if (handle instanceof MessageSwitchServerHandle) {
      new MessageSwitchHandleServer(
          messageSwitchService, connect, (MessageSwitchServerHandle) handle);
    }
    if (handle instanceof AccountServerHandle) {
      connect.setAccountHandleServer(
          new AccountHandleServer(accountService, (AccountServerHandle) handle));
    }
  }

}
