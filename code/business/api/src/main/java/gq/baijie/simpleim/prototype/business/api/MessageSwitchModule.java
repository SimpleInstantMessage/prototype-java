package gq.baijie.simpleim.prototype.business.api;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class MessageSwitchModule {

  @Provides
  @Singleton
  MessageSwitchService provideMessageSwitchService(MemoryMessageSwitchService service) {
    return service;
  }

}
