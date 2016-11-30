package gq.baijie.simpleim.prototype.business.server;

import dagger.Module;
import dagger.Provides;

@Module
public class MockServerModule {

  @Provides
  Server provideServer(MockServer mockServer) {
    return mockServer;
  }

}
