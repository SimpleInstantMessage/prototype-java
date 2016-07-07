package gq.baijie.simpleim.prototype.io.network.netty.client.inject;

import dagger.Subcomponent;
import gq.baijie.simpleim.prototype.io.network.api.Client;

@ClientScope
@Subcomponent(modules = NettyClientModule.class)
public interface ClientComponent {

  Client getClient();

}
