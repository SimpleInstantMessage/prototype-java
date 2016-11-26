package gq.baijie.simpleim.prototype.server.impl.vertx;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import gq.baijie.simpleim.prototype.business.api.MessageSwitchService;

@Module
public class RemoteMessageSwitchModule {

  @Provides
  @Singleton
  MessageSwitchService provideMessageSwitchService() {
    return new RemoteMessageSwitchService();
  }

}
