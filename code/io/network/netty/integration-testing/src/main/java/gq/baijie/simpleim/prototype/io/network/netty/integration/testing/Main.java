package gq.baijie.simpleim.prototype.io.network.netty.integration.testing;

import gq.baijie.simpleim.prototype.business.api.AccountService;
import gq.baijie.simpleim.prototype.business.api.Result;
import gq.baijie.simpleim.prototype.io.network.api.Client;
import gq.baijie.simpleim.prototype.io.network.api.Server;
import gq.baijie.simpleim.prototype.io.network.api.service.EchoService;
import gq.baijie.simpleim.prototype.io.network.api.service.ServerManageService;
import gq.baijie.simpleim.prototype.io.network.netty.client.inject.ClientComponent;
import gq.baijie.simpleim.prototype.io.network.netty.integration.testing.inject.DaggerServiceComponent;
import gq.baijie.simpleim.prototype.io.network.netty.integration.testing.inject.ServiceComponent;
import gq.baijie.simpleim.prototype.io.network.netty.server.service.SystemManagerService;
import rx.functions.Action1;

public class Main {

  public static final Main INSTANCE = new Main();

  public ServiceComponent serviceComponent = DaggerServiceComponent.create();

  public static void main(String[] args) {

    System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "debug");
    System.setProperty("org.slf4j.simpleLogger.logFile", "log.log");
//    InternalLoggerFactory.setDefaultFactory(Slf4JLoggerFactory.INSTANCE);

    final ServiceComponent serviceComponent = INSTANCE.serviceComponent;
    final SystemManagerService systemManagerService = serviceComponent.getSystemManagerService();
    final Server server = serviceComponent.newServer();
    systemManagerService.getStartTaskList().add(() -> server.listen(56789));
    systemManagerService.getStopTasksList().add(server::stop);

    systemManagerService.start();

    final ClientComponent clientComponent = serviceComponent.newClientComponent();
    final Client client = clientComponent.getClient();
    client.connect("localhost", 56789);

    client.getService(EchoService.class)
        .echo("test echo".getBytes()).subscribe(result -> {
      if (result.succeeded()) {
        System.out.println("echo succeeded response:");
        System.out.println(new String(result.result()));
      } else {
        System.err.println(result.error());
      }
    });

    final Action1<Result<Void, AccountService.CreateError>> createHandler = result -> {
      if (result.succeeded()) {
        System.out.println("create Account succeeded");
      } else {
        System.err.println(result.error());
      }
    };
    client.getService(AccountService.class).create("test", "testpassword").subscribe(createHandler);
    client.getService(AccountService.class).create("baijie", "testpassword").subscribe(createHandler);

    client.getService(ServerManageService.class).shutdown().subscribe(result -> {
      if (result.succeeded()) {
        System.out.println("shutdown server succeeded");
      } else {
        System.err.println(result.error());
      }
    });

  }

}
