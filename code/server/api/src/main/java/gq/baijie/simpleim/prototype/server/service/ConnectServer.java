package gq.baijie.simpleim.prototype.server.service;

import java.util.LinkedList;
import java.util.List;

import javax.inject.Inject;

import gq.baijie.simpleim.prototype.server.service.Server.Connect;

public class ConnectServer {

  @Inject
  MessageSwitchServer messageSwitchServer;
  @Inject
  AccountServer accountServer;

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
      messageSwitchServer.onReceiveHandle(connect, (MessageSwitchServerHandle) handle);
    }
    if (handle instanceof AccountServerHandle) {
      connect.setAccountHandleServer(accountServer.onReceiveHandle((AccountServerHandle) handle));
    }
  }

}
