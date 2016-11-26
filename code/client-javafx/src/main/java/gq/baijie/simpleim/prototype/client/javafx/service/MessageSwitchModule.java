package gq.baijie.simpleim.prototype.client.javafx.service;

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
