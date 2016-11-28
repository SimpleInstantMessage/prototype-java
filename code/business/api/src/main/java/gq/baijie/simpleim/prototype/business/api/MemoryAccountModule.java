package gq.baijie.simpleim.prototype.business.api;

import dagger.Module;
import dagger.Provides;

@Module
public class MemoryAccountModule {

  @Provides
  AccountService provideAccountService(MemoryAccountService service) {
    return service;
  }

}
