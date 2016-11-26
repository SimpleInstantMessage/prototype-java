package gq.baijie.simpleim.prototype.server.service;

import javax.inject.Inject;

import gq.baijie.simpleim.prototype.business.api.MessageSwitchService;

public class MessageSwitchServer {

  @Inject
  MessageSwitchService messageSwitchService;

  @Inject
  public MessageSwitchServer() {
  }

  public void onReceiveMessageSwitchHandle(MessageSwitchService.Session event) {
    final MessageSwitchService.Session session = messageSwitchService.connect();
    session.receiveMessages().subscribe(event::sendMessage);
    event.receiveMessages().subscribe(session::sendMessage);
  }

}
