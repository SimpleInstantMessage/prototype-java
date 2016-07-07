package gq.baijie.simpleim.prototype.io.network.netty.client.inject;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;
import gq.baijie.simpleim.prototype.io.network.api.Client;
import gq.baijie.simpleim.prototype.io.network.netty.client.NettyClient;
import gq.baijie.simpleim.prototype.io.network.netty.client.service.AccountServiceImpl;
import gq.baijie.simpleim.prototype.io.network.netty.client.service.EchoServiceImpl;
import gq.baijie.simpleim.prototype.io.network.netty.client.service.ServerManageServiceImpl;
import gq.baijie.simpleim.prototype.io.network.netty.common.handler.MessageFrameInboundHandler2;

@Module
public class NettyClientModule {

  @Provides
  @ClientScope
  Client provideClient(NettyClient nettyClient) {
    return nettyClient;
  }

  @Provides
  @ClientScope
  MessageFrameInboundHandler2 provideBusinessHandler() {
    return new MessageFrameInboundHandler2();
  }

  @Provides
  @ClientScope
  @Named("services")
  Object[] provideServices(
      EchoServiceImpl echoService,
      ServerManageServiceImpl serverManageService,
      AccountServiceImpl accountService
  ) {
    return new Object[]{echoService, serverManageService, accountService};
  }

}
