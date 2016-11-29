package gq.baijie.simpleim.prototype.server.impl.vertx;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;

import javax.inject.Inject;

import gq.baijie.simpleim.prototype.business.api.MessageSwitchService;
import gq.baijie.simpleim.prototype.server.impl.vertx.codec.RecordCodec;
import io.vertx.core.Vertx;
import io.vertx.core.net.NetClient;
import io.vertx.core.net.NetClientOptions;
import io.vertx.core.net.NetSocket;

public class RemoteMessageSwitchService implements MessageSwitchService {
  private final Logger logger = LoggerFactory.getLogger(RemoteMessageSwitchService.class);

  @Inject
  RecordCodec recordCodec;

  private final Vertx vertx = Vertx.vertx();

  @Inject
  public RemoteMessageSwitchService() {
  }

  @Override
  public MessageSwitchService.Session connect() {
//    System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "debug");
//    System.setProperty("org.slf4j.simpleLogger.logFile", "log-client.log");
    NetClientOptions options = new NetClientOptions()
//        .setLogActivity(true)
        .setConnectTimeout(10000);
    NetClient client = vertx.createNetClient(options);

    CompletableFuture<Session> session = new CompletableFuture<>();
    client.connect(4321, "localhost", res -> {
      if (res.succeeded()) {
        logger.info("Connected!");
        NetSocket socket = res.result();
        session.complete(new VertxClientSession(client, socket, recordCodec));
      } else {
        logger.info("Failed to connect: " + res.cause().getMessage());
        session.completeExceptionally(res.cause());
      }
    });

    try {
      return session.get();
    } catch (Exception e) {
      logger.error("cannot connect", e);//TODO improve
      return null;
    }
  }

  static class VertxClientSession extends VertxSession {

    private final NetClient client;

    public VertxClientSession(NetClient client, NetSocket socket, RecordCodec recordCodec) {
      super(socket, recordCodec);
      this.client = client;
    }

    @Override
    public void close() {
      super.close();
      client.close();
    }
  }

}
