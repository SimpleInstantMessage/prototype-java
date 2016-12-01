package gq.baijie.simpleim.prototype.impl.vertx.client;

import dagger.Module;
import dagger.Provides;
import gq.baijie.simpleim.prototype.business.client.AccountService;

@Module
public class RemoteAccountServiceModule {

  @Provides
  AccountService provideAccountService(RemoteAccountService service) {
    return service;
  }

}
