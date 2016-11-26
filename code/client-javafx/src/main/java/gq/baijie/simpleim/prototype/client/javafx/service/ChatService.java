package gq.baijie.simpleim.prototype.client.javafx.service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Singleton;

import gq.baijie.simpleim.prototype.business.api.Message;
import gq.baijie.simpleim.prototype.business.api.MessageSwitchService;
import rx.Observable;

@Singleton
public class ChatService {

  private final MessageSwitchService messageSwitchService;
  private final MessageSwitchService.Session messageSwitchSession;

  @Inject
  public ChatService(MessageSwitchService messageSwitchService) {
    this.messageSwitchService = messageSwitchService;
    messageSwitchSession = messageSwitchService.connect();
  }

  Observable<Message> newMessageEventBus() {
    return messageSwitchSession.receiveMessages();
  }

  Message sendMessage(String senderId, String message, Set<String> receiverIds) {
    final List<Message.Receiver> receivers = receiverIds.stream()
        .map(Message.Receiver::new)
        .collect(Collectors.toList());
    final Message newMessage = new Message(senderId, receivers, message);
    messageSwitchSession.sendMessage(newMessage);
    return newMessage;
  }

}
