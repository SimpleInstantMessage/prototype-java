package gq.baijie.simpleim.prototype.server.service;

import javax.inject.Inject;

import gq.baijie.simpleim.prototype.business.api.MessageSwitchService;

public class MessageSwitchServer {

  @Inject
  MessageSwitchService messageSwitchService;

  @Inject
  public MessageSwitchServer() {
  }

  public void onReceiveHandler(MessageSwitchServerHandle handle) {
    final MessageSwitchService.Session session = messageSwitchService.connect();
    session.receiveMessages().subscribe(handle::sendMessage);
    handle.setOnReceiveRequestListener(session::sendMessage);
  }

}
