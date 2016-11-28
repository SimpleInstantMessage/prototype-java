package gq.baijie.simpleim.prototype.client.javafx.inject;

import javax.inject.Singleton;

import dagger.Component;
import gq.baijie.simpleim.prototype.business.api.AccountService;
import gq.baijie.simpleim.prototype.client.javafx.service.SessionService;
import gq.baijie.simpleim.prototype.server.impl.vertx.RemoteAccountServiceModule;
import gq.baijie.simpleim.prototype.server.impl.vertx.RemoteMessageSwitchModule;
import gq.baijie.simpleim.prototype.server.impl.vertx.codec.CodecModule;

@Singleton
@Component(modules = {
//    MemoryAccountModule.class,
    CodecModule.class,
    RemoteAccountServiceModule.class,
//    MessageSwitchModule.class
    RemoteMessageSwitchModule.class
})
public interface ServiceComponent {

  SessionService getSessionService();

  AccountService getAccountService();

}
