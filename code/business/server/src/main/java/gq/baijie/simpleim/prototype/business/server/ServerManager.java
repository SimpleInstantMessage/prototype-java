package gq.baijie.simpleim.prototype.business.server;

import java.util.LinkedList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import gq.baijie.simpleim.prototype.business.common.AccountService;
import gq.baijie.simpleim.prototype.business.common.MessageSwitchService;
import gq.baijie.simpleim.prototype.business.server.Server.Connect;
import gq.baijie.simpleim.prototype.business.server.session.AccountSession;
import gq.baijie.simpleim.prototype.business.server.session.AccountSessionHandler;
import gq.baijie.simpleim.prototype.business.server.session.MessageSwitchSession;
import gq.baijie.simpleim.prototype.business.server.session.MessageSwitchSessionHandler;

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
    server.connects().subscribe(connect -> onNewConnect(connect, connects));
    //TODO let server weak reference to connects
    final ManagedServer managedServer = new ManagedServer(server, connects);
    servers.add(managedServer);
    return managedServer;
  }

  private void onNewConnect(Connect connect, List<ManagedConnect> connects) {
    final ManagedConnect managedConnect = new ManagedConnect(connect);
    connects.add(managedConnect);
    managedConnect.getCloseEvents().subscribe(connects::remove);
    connect.sessions().subscribe(session -> onNewHandle(managedConnect, session));
  }

  private void onNewHandle(ManagedConnect connect, Object handle) {
    if (handle instanceof MessageSwitchSession) {
      connect.registerSessionHandler(
          new MessageSwitchSessionHandler(messageSwitchService, (MessageSwitchSession) handle));
    }
    if (handle instanceof AccountSession) {
      connect.registerSessionHandler(
          new AccountSessionHandler(accountService, (AccountSession) handle));
    }
  }

  public void startAllServer() {
    servers.forEach(ManagedServer::start);
  }

  public void stopAllServer() {
    servers.forEach(ManagedServer::stop);
  }

}
