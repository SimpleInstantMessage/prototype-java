package gq.baijie.simpleim.prototype.server;

import gq.baijie.simpleim.prototype.business.server.Server;
import gq.baijie.simpleim.prototype.business.server.ServerManager;

public class Main implements Runnable {

  public static final Main INSTANCE = new Main();

  public ServerComponent serverComponent = DaggerServerComponent.create();

  public static void main(String[] args) {
    INSTANCE.run();
  }

  @Override
  public void run() {
    final Server server = serverComponent.getServer();
    final ServerManager serverManager = serverComponent.getServerManager();
    serverManager.manage(server);
    serverManager.startAllServer();
  }

}
