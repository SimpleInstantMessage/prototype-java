package gq.baijie.simpleim.prototype.impl.vertx;

import dagger.Module;
import dagger.Provides;
import gq.baijie.simpleim.prototype.business.common.AccountService;

@Module
public class RemoteAccountServiceModule {

  @Provides
  AccountService provideAccountService(RemoteAccountService service) {
    return service;
  }

}
