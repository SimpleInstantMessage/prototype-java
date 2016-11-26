package gq.baijie.simpleim.prototype.client.javafx.service;

import rx.Observable;

public interface MessageSwitchService {

  Session connect();

  interface Session {

    void sendMessage(ChatService.Message message);

    Observable<ChatService.Message> receiveMessages();

    void close();
  }

}
