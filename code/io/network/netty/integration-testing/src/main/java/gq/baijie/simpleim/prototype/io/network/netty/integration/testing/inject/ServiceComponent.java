package gq.baijie.simpleim.prototype.io.network.netty.integration.testing.inject;

import javax.inject.Singleton;

import dagger.Component;
import gq.baijie.simpleim.prototype.io.network.netty.client.service.NettyClientService;
import gq.baijie.simpleim.prototype.io.network.netty.server.inject.NetworkIoModule;
import gq.baijie.simpleim.prototype.io.network.netty.server.service.AccountService;
import gq.baijie.simpleim.prototype.io.network.netty.server.service.NettyServerService;
import gq.baijie.simpleim.prototype.io.network.netty.server.service.SessionService;
import gq.baijie.simpleim.prototype.io.network.netty.server.service.SystemManagerService;
import gq.baijie.simpleim.prototype.io.network.netty.server.service.ThreadService;

@Singleton
@Component(modules = NetworkIoModule.class)
public interface ServiceComponent {

  ThreadService getThreadService();

  SystemManagerService getSystemManagerService();

  NettyServerService getNettyServerService();

  NettyClientService newNettyClientService();

  SessionService getSessionService();

  AccountService getAccountService();

}
