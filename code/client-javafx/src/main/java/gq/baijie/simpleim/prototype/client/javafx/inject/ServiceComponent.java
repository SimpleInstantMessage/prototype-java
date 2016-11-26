package gq.baijie.simpleim.prototype.client.javafx.inject;

import javax.inject.Singleton;

import dagger.Component;
import gq.baijie.simpleim.prototype.business.api.MessageSwitchModule;
import gq.baijie.simpleim.prototype.client.javafx.service.AccountService;
import gq.baijie.simpleim.prototype.client.javafx.service.SessionService;

@Singleton
@Component(modules = MessageSwitchModule.class)
public interface ServiceComponent {

  SessionService getSessionService();

  AccountService getAccountService();

}
