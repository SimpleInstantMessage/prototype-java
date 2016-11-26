package gq.baijie.simpleim.prototype.server;

import gq.baijie.simpleim.prototype.server.service.HandleServer;
import gq.baijie.simpleim.prototype.server.service.Server;

public class Main implements Runnable {

  public static final Main INSTANCE = new Main();

  public ServerComponent serverComponent = DaggerServerComponent.create();

  public static void main(String[] args) {
    INSTANCE.run();
  }

  @Override
  public void run() {
    final Server server = serverComponent.getServer();
    final HandleServer handleServer = serverComponent.getHandleServer();
    server.connects().subscribe(r -> r.handles().subscribe(handleServer::onNewHandle));
    server.start();
  }

}
