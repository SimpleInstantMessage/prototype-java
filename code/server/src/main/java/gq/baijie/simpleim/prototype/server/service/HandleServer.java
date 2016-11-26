package gq.baijie.simpleim.prototype.server.service;

import javax.inject.Inject;

import gq.baijie.simpleim.prototype.business.api.MessageSwitchService;

public class HandleServer {

  @Inject
  MessageSwitchServer messageSwitchServer;

  @Inject
  public HandleServer() {
  }

  public void onNewHandle(Object handle) {
    if (handle instanceof MessageSwitchService.Session) {
      messageSwitchServer.onReceiveMessageSwitchHandle((MessageSwitchService.Session) handle);
    }
  }

}
