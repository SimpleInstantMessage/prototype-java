package gq.baijie.simpleim.prototype.impl.vertx.server;

import dagger.Module;
import dagger.Provides;
import gq.baijie.simpleim.prototype.business.server.Server;

@Module
public class VertxServerModule {

  @Provides
  Server provideServer(VertxServer server) {
    return server;
  }

}
