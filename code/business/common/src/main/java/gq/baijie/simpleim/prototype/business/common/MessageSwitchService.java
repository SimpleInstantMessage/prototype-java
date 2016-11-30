package gq.baijie.simpleim.prototype.business.common;

import rx.Observable;

public interface MessageSwitchService {

  Session connect();

  interface Session {

    void sendMessage(Message message);

    Observable<Message> receiveMessages();

    void close();
  }

}
