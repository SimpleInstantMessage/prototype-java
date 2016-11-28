package gq.baijie.simpleim.prototype.server.impl.vertx;

import dagger.Module;
import dagger.Provides;
import gq.baijie.simpleim.prototype.business.api.AccountService;

@Module
public class RemoteAccountServiceModule {

  @Provides
  AccountService provideAccountService(RemoteAccountService service) {
    return service;
  }

}
