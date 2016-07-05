package gq.baijie.simpleim.prototype.server;

import gq.baijie.simpleim.prototype.server.inject.DaggerServiceComponent;
import gq.baijie.simpleim.prototype.server.inject.ServiceComponent;
import gq.baijie.simpleim.prototype.server.service.NettyClientService;
import gq.baijie.simpleim.prototype.server.service.NettyServerService;
import gq.baijie.simpleim.prototype.server.service.SystemManagerService;

public class Main {

  public static final Main INSTANCE = new Main();

  public ServiceComponent serviceComponent = DaggerServiceComponent.create();

  public static void main(String[] args) {

    System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "debug");
    System.setProperty("org.slf4j.simpleLogger.logFile", "log.log");
//    InternalLoggerFactory.setDefaultFactory(Slf4JLoggerFactory.INSTANCE);

    final ServiceComponent serviceComponent = INSTANCE.serviceComponent;
    final SystemManagerService systemManagerService = serviceComponent.getSystemManagerService();
    final NettyServerService nettyServerService = serviceComponent.getNettyServerService();
    systemManagerService.getStartTaskList().add(() -> nettyServerService.start(56789));
    systemManagerService.getStopTasksList().add(nettyServerService::stop);

    systemManagerService.start();

    final NettyClientService nettyClientService = serviceComponent.newNettyClientService();
    nettyClientService.start("localhost", 56789);
    nettyClientService.sendEchoRequest();
    nettyClientService.sendEchoRequest();
    nettyClientService.sendEchoRequest();
    nettyClientService.sendShutdownServerRequest();

  }

  private static void println(Object o) {
    System.out.println(o);
  }

}
