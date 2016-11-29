package gq.baijie.simpleim.prototype.server.service;

import javax.inject.Inject;

import gq.baijie.simpleim.prototype.business.api.MessageSwitchService;

public class HandleServer {

  @Inject
  MessageSwitchServer messageSwitchServer;
  @Inject
  AccountServer accountServer;
  @Inject
  AccountServer2 accountServer2;

  @Inject
  public HandleServer() {
  }

  public void onNewHandle(Object handle) {
    if (handle instanceof MessageSwitchService.Session) {
      messageSwitchServer.onReceiveMessageSwitchHandle((MessageSwitchService.Session) handle);
    }
    if (handle instanceof AccountServerHandle) {
      accountServer.onReceiveHandler((AccountServerHandle) handle);
    }
    if (handle instanceof AccountServerHandle2) {
      accountServer2.onReceiveHandler((AccountServerHandle2) handle);
    }
  }

}
