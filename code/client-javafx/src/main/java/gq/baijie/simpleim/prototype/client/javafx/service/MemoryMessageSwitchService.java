package gq.baijie.simpleim.prototype.client.javafx.service;

import javax.inject.Inject;

import rx.Observable;
import rx.subjects.PublishSubject;

public class MemoryMessageSwitchService implements MessageSwitchService {

  private final PublishSubject<ChatService.Message> messageBus = PublishSubject.create();

  @Inject
  public MemoryMessageSwitchService() {
  }

  @Override
  public Session connect() {
    return new Session();
  }

  public class Session implements MessageSwitchService.Session {

    @Override
    public void sendMessage(ChatService.Message message) {
      messageBus.onNext(message);
    }

    @Override
    public Observable<ChatService.Message> receiveMessages() {
      return messageBus.asObservable();
    }

    @Override
    public void close() {
      //TODO release all subscription to receiveMessages() method
    }
  }

}
