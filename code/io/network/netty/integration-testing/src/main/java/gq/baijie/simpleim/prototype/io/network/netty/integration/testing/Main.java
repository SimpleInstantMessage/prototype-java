package gq.baijie.simpleim.prototype.io.network.netty.integration.testing;

import gq.baijie.simpleim.prototype.io.network.netty.client.service.NettyClientService;
import gq.baijie.simpleim.prototype.io.network.netty.integration.testing.inject.DaggerServiceComponent;
import gq.baijie.simpleim.prototype.io.network.netty.integration.testing.inject.ServiceComponent;
import gq.baijie.simpleim.prototype.io.network.netty.server.service.NettyServerService;
import gq.baijie.simpleim.prototype.io.network.netty.server.service.SystemManagerService;

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
    nettyClientService.sendCreateAccountRequest("test", "testpassword");
    nettyClientService.sendCreateAccountRequest("baijie", "testpassword");
    nettyClientService.sendShutdownServerRequest();

  }

}
