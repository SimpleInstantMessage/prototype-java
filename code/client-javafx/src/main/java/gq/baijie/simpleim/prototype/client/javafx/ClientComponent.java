package gq.baijie.simpleim.prototype.client.javafx;

import javax.inject.Singleton;

import dagger.Component;
import gq.baijie.simpleim.prototype.business.common.AccountService;
import gq.baijie.simpleim.prototype.business.client.SessionService;
import gq.baijie.simpleim.prototype.business.common.ApplicationService;
import gq.baijie.simpleim.prototype.impl.vertx.RemoteAccountServiceModule;
import gq.baijie.simpleim.prototype.impl.vertx.RemoteMessageSwitchModule;
import gq.baijie.simpleim.prototype.impl.vertx.codec.CodecModule;

@Singleton
@Component(modules = {
//    MemoryAccountModule.class,
    CodecModule.class,
    RemoteAccountServiceModule.class,
//    MessageSwitchModule.class
    RemoteMessageSwitchModule.class
})
public interface ClientComponent {

  ApplicationService getApplicationService();

  SessionService getSessionService();

  AccountService getAccountService();

}