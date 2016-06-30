package gq.baijie.simpleim.prototype.server;

import gq.baijie.simpleim.prototype.server.inject.DaggerServiceComponent;
import gq.baijie.simpleim.prototype.server.inject.ServiceComponent;

public class Main {

  public static final Main INSTANCE = new Main();

  public ServiceComponent serviceComponent = DaggerServiceComponent.create();

  public static void main(String[] args) {

    println(INSTANCE.serviceComponent.getAccountService());
    println(INSTANCE.serviceComponent.getAccountService());
    println(INSTANCE.serviceComponent.getSessionService());
    println(INSTANCE.serviceComponent.getSessionService());

  }

  private static void println(Object o) {
    System.out.println(o);
  }

}
