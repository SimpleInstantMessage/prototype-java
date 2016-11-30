package gq.baijie.simpleim.prototype.server.service;

import javax.inject.Inject;

import gq.baijie.simpleim.prototype.business.api.MessageSwitchService;

public class MessageSwitchServer {

  @Inject
  MessageSwitchService messageSwitchService;

  @Inject
  public MessageSwitchServer() {
  }

  public void onReceiveHandle(ManagedConnect connect, MessageSwitchServerHandle handle) {
    new MessageSwitchHandleServer(messageSwitchService, connect, handle);
  }

}
