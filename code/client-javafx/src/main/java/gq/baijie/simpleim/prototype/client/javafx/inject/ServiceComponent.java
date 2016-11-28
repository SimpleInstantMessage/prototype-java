package gq.baijie.simpleim.prototype.client.javafx.inject;

import javax.inject.Singleton;

import dagger.Component;
import gq.baijie.simpleim.prototype.business.api.AccountService;
import gq.baijie.simpleim.prototype.client.javafx.service.SessionService;
import gq.baijie.simpleim.prototype.server.impl.vertx.RemoteMessageSwitchModule;

@Singleton
@Component(modules = {
//    MessageSwitchModule.class
    RemoteMessageSwitchModule.class
})
public interface ServiceComponent {

  SessionService getSessionService();

  AccountService getAccountService();

}
