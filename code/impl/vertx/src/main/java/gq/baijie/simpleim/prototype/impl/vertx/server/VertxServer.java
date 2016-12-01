package gq.baijie.simpleim.prototype.impl.vertx.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;

import gq.baijie.simpleim.prototype.impl.vertx.codec.RecordCodec;
import gq.baijie.simpleim.prototype.business.server.Server;
import io.vertx.core.Vertx;
import io.vertx.core.net.NetServer;
import io.vertx.core.net.NetServerOptions;
import rx.Observable;
import rx.subjects.PublishSubject;

public class VertxServer implements Server {

  private final Logger logger = LoggerFactory.getLogger(VertxServer.class);

  @Inject
  RecordCodec recordCodec;

  private final PublishSubject<Connect> connects = PublishSubject.create();

  private final Vertx vertx = Vertx.vertx();
  private NetServer server;

  @Inject
  public VertxServer() {
  }

  @Override
  public void start() {

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
      NetSocketConnect connect = new NetSocketConnect(socket, recordCodec);
      connect.addSession(new VertxAccountSession(connect));
      connect.addSession(new VertxMessageSwitchSession(connect));
      connects.onNext(connect);
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
  }

  @Override
  public Observable<Connect> connects() {
    return connects;
  }

}
