package gq.baijie.simpleim.prototype.business.server.mock;

import dagger.Module;
import dagger.Provides;
import gq.baijie.simpleim.prototype.business.server.Server;

@Module
public class MockServerModule {

  @Provides
  Server provideServer(MockServer mockServer) {
    return mockServer;
  }

}
