package gq.baijie.simpleim.prototype.business.common;

import dagger.Module;
import dagger.Provides;

@Module
public class MemoryAccountModule {

  @Provides
  AccountService provideAccountService(MemoryAccountService service) {
    return service;
  }

}
