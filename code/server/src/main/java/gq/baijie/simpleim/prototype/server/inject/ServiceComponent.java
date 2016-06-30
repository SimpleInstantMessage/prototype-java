package gq.baijie.simpleim.prototype.server.inject;

import javax.inject.Singleton;

import dagger.Component;
import gq.baijie.simpleim.prototype.server.service.AccountService;
import gq.baijie.simpleim.prototype.server.service.SessionService;

@Singleton
@Component
public interface ServiceComponent {

  SessionService getSessionService();

  AccountService getAccountService();

}
