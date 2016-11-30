package gq.baijie.simpleim.prototype.server;

import javax.inject.Singleton;

import dagger.Component;
import gq.baijie.simpleim.prototype.business.common.MemoryAccountModule;
import gq.baijie.simpleim.prototype.business.common.MessageSwitchModule;
import gq.baijie.simpleim.prototype.impl.vertx.VertxServerModule;
import gq.baijie.simpleim.prototype.impl.vertx.codec.CodecModule;
import gq.baijie.simpleim.prototype.business.server.ConnectServer;
import gq.baijie.simpleim.prototype.business.server.Server;

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

  ConnectServer getConnectServer();

}
