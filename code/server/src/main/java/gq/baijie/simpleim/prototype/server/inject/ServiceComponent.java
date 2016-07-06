package gq.baijie.simpleim.prototype.server.inject;

import javax.inject.Singleton;

import dagger.Component;
import gq.baijie.simpleim.prototype.server.service.AccountService;
import gq.baijie.simpleim.prototype.server.service.NettyClientService;
import gq.baijie.simpleim.prototype.server.service.NettyServerService;
import gq.baijie.simpleim.prototype.server.service.SessionService;
import gq.baijie.simpleim.prototype.server.service.SystemManagerService;
import gq.baijie.simpleim.prototype.server.service.ThreadService;

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
