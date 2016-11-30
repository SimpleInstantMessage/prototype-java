package gq.baijie.simpleim.prototype.server;

import gq.baijie.simpleim.prototype.server.service.ConnectServer;
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
    final ConnectServer connectServer = serverComponent.getConnectServer();
    server.connects().subscribe(connectServer::onNewConnect);
    server.start();
  }

}
