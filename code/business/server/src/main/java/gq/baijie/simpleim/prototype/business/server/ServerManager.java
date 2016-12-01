package gq.baijie.simpleim.prototype.business.server;

import java.util.LinkedList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import gq.baijie.simpleim.prototype.business.common.AccountService;
import gq.baijie.simpleim.prototype.business.common.MessageSwitchService;
import gq.baijie.simpleim.prototype.business.server.Server.Connect;

@Singleton
public class ServerManager {

  @Inject
  MessageSwitchService messageSwitchService;
  @Inject
  AccountService accountService;

  private final List<ManagedServer> servers = new LinkedList<>();

  @Inject
  public ServerManager() {
  }

  public ManagedServer manage(Server server) {
    final List<ManagedConnect> connects = new LinkedList<>();
    server.connects().subscribe(connect -> onNewConnect(connect, connects));//TODO
    final ManagedServer managedServer = new ManagedServer(server, connects);
    servers.add(managedServer);
    return managedServer;
  }

  private void onNewConnect(Connect connect, List<ManagedConnect> connects) {
    final ManagedConnect managedConnect = new ManagedConnect(connect);
    connects.add(managedConnect);
    managedConnect.getCloseEvents().subscribe(connects::remove);
    connect.handles().subscribe(handle -> onNewHandle(managedConnect, handle));
  }

  private void onNewHandle(ManagedConnect connect, Object handle) {
    if (handle instanceof MessageSwitchServerHandle) {
      connect.registerHandleServer(
          new MessageSwitchHandleServer(messageSwitchService, (MessageSwitchServerHandle) handle));
    }
    if (handle instanceof AccountServerHandle) {
      connect.registerHandleServer(
          new AccountHandleServer(accountService, (AccountServerHandle) handle));
    }
  }

  public void startAllServer() {
    servers.forEach(ManagedServer::start);
  }

  public void stopAllServer() {
    servers.forEach(ManagedServer::stop);
  }

}
