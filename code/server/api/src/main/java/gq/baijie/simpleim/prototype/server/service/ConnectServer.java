package gq.baijie.simpleim.prototype.server.service;

import java.util.LinkedList;
import java.util.List;

import javax.inject.Inject;

import gq.baijie.simpleim.prototype.server.service.Server.Connect;

public class ConnectServer {

  @Inject
  HandleServer handleServer;

  private final List<ManagedConnect> connects = new LinkedList<>();

  @Inject
  public ConnectServer() {
  }

  public void onNewConnect(Connect connect) {
    final ManagedConnect managedConnect = new ManagedConnect(connect);
    connects.add(managedConnect);
    connect.setOnCloseListener(c -> connects.remove(managedConnect));
    connect.handles().subscribe(handleServer::onNewHandle);
  }

  private static class ManagedConnect {
    private final Connect connect;

    private ManagedConnect(Connect connect) {
      this.connect = connect;
    }
  }

}
