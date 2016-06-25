package gq.baijie.simpleim.prototype.client.javafx.inject;

import javax.inject.Singleton;

import dagger.Component;
import gq.baijie.simpleim.prototype.client.javafx.service.AccountService;
import gq.baijie.simpleim.prototype.client.javafx.service.SessionService;

@Singleton
@Component
public interface ServiceComponent {

  SessionService getSessionService();

  AccountService getAccountService();

}
