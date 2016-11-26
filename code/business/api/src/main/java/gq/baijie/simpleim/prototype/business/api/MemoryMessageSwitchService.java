package gq.baijie.simpleim.prototype.business.api;

import javax.inject.Inject;

import rx.Observable;
import rx.subjects.PublishSubject;

public class MemoryMessageSwitchService implements MessageSwitchService {

  private final PublishSubject<Message> messageBus = PublishSubject.create();

  @Inject
  public MemoryMessageSwitchService() {
  }

  @Override
  public Session connect() {
    return new Session();
  }

  public class Session implements MessageSwitchService.Session {

    @Override
    public void sendMessage(Message message) {
      messageBus.onNext(message);
    }

    @Override
    public Observable<Message> receiveMessages() {
      return messageBus.asObservable();
    }

    @Override
    public void close() {
      //TODO release all subscription to receiveMessages() method
    }
  }

}
