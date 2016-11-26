package gq.baijie.simpleim.prototype.business.api;

import rx.Observable;

public interface MessageSwitchService {

  Session connect();

  interface Session {

    void sendMessage(Message message);

    Observable<Message> receiveMessages();

    void close();
  }

}
