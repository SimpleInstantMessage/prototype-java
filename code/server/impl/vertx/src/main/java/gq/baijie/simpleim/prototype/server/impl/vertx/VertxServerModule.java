package gq.baijie.simpleim.prototype.server.impl.vertx;

import dagger.Module;
import dagger.Provides;
import gq.baijie.simpleim.prototype.server.service.Server;

@Module
public class VertxServerModule {

  @Provides
  Server provideServer(VertxServer server) {
    return server;
  }

}
