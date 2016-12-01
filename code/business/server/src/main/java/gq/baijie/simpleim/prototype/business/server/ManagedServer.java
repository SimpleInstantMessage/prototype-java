package gq.baijie.simpleim.prototype.business.server;

import java.util.List;

public class ManagedServer {

  private final Server server;

  private final List<ManagedConnect> connects;

  public ManagedServer(Server server, List<ManagedConnect> connects) {
    this.server = server;
    this.connects = connects;
  }

  public void start() {
    server.start();
  }

  public void stop() {
    server.stop();
  }

}
