package gq.baijie.simpleim.prototype.server.impl.vertx;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;

import gq.baijie.simpleim.prototype.server.impl.vertx.codec.RecordCodec;
import gq.baijie.simpleim.prototype.server.service.Server;
import io.vertx.core.Vertx;
import io.vertx.core.net.NetServer;
import io.vertx.core.net.NetServerOptions;
import rx.Observable;
import rx.subjects.PublishSubject;

public class VertxServer implements Server {

  private final Logger logger = LoggerFactory.getLogger(VertxServer.class);

  @Inject
  RecordCodec recordCodec;

  private final PublishSubject<NewConnectEvent> connects = PublishSubject.create();

  private final Vertx vertx = Vertx.vertx();
  private NetServer server;
  private NetServer accountServer;

  @Inject
  public VertxServer() {
  }

  @Override
  public void start() {
    startAccountServer();

    if (server != null) {
      return;
    }
//    System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "debug");
//    System.setProperty("org.slf4j.simpleLogger.logFile", "log-server.log");
    NetServerOptions options = new NetServerOptions()
//        .setLogActivity(true)
        .setPort(4321);
    server = vertx.createNetServer(options);

    server.connectHandler(socket -> {
      connects.onNext(() -> Observable.just(new VertxMessageSwitchServerHandle(socket)));
    });

    server.listen(/*4321, */res -> {
      if (res.succeeded()) {
        logger.info("Server is now listening!");
      } else {
        logger.info("Failed to bind!");
      }
    });
  }

  @Override
  public void stop() {
    if (server == null) {
      return;
    }
    server.close();
    server = null;

    stopAccountServer();
  }

  private void startAccountServer() {
    if (accountServer != null) {
      return;
    }
    accountServer = vertx.createNetServer();

    accountServer.connectHandler(socket -> {
      connects.onNext(() -> Observable.just(new VertxAccountServerHandle(socket, recordCodec)));
    });

    accountServer.listen(4322, res -> {
      if (res.succeeded()) {
        logger.info("Account Server is now listening!");
      } else {
        logger.info("Failed to bind Account Server!");
      }
    });
  }
  private void stopAccountServer() {
    if (accountServer != null) {
      accountServer.close();
      accountServer = null;
    }
  }

  @Override
  public Observable<NewConnectEvent> connects() {
    return connects;
  }

}
