package gq.baijie.simpleim.prototype.server.service;

import dagger.Module;
import dagger.Provides;

@Module
public class MockServerModule {

  @Provides
  Server provideServer(MockServer mockServer) {
    return mockServer;
  }

}
