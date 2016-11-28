package gq.baijie.simpleim.prototype.server;

import javax.inject.Singleton;

import dagger.Component;
import gq.baijie.simpleim.prototype.business.api.MemoryAccountModule;
import gq.baijie.simpleim.prototype.business.api.MessageSwitchModule;
import gq.baijie.simpleim.prototype.server.impl.vertx.VertxServerModule;
import gq.baijie.simpleim.prototype.server.impl.vertx.codec.CodecModule;
import gq.baijie.simpleim.prototype.server.service.HandleServer;
import gq.baijie.simpleim.prototype.server.service.Server;

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

  HandleServer getHandleServer();

}
