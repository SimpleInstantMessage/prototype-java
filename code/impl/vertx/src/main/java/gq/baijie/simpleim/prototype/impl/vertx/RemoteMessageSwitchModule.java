package gq.baijie.simpleim.prototype.impl.vertx;

import dagger.Module;
import dagger.Provides;
import gq.baijie.simpleim.prototype.business.common.MessageSwitchService;

@Module
public class RemoteMessageSwitchModule {

  @Provides
  MessageSwitchService provideMessageSwitchService(RemoteMessageSwitchService service) {
    return service;
  }

}
