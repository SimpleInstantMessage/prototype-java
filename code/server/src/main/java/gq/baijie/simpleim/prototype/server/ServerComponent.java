package gq.baijie.simpleim.prototype.server;

import javax.inject.Singleton;

import dagger.Component;
import gq.baijie.simpleim.prototype.business.common.MemoryAccountModule;
import gq.baijie.simpleim.prototype.business.common.MessageSwitchModule;
import gq.baijie.simpleim.prototype.business.server.Server;
import gq.baijie.simpleim.prototype.business.server.ServerManager;
import gq.baijie.simpleim.prototype.impl.vertx.VertxServerModule;
import gq.baijie.simpleim.prototype.impl.vertx.codec.CodecModule;

@Singleton
@Component(modules = {
    MessageSwitchModule.class,
    MemoryAccountModule.class,
    CodecModule.class,
//    MockServerModule.class
    VertxServerModule.class
})
public interface ServerComponent {

  Server getServer();

  ServerManager getServerManager();

}
