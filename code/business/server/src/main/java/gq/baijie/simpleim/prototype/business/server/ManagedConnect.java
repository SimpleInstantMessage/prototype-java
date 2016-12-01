package gq.baijie.simpleim.prototype.business.server;

import java.util.HashMap;
import java.util.Map;

import gq.baijie.simpleim.prototype.business.server.Server.Connect;
import rx.Observable;
import rx.subjects.PublishSubject;

public class ManagedConnect {

  private final Connect connect;

  private final PublishSubject<ManagedConnect> closeEvents = PublishSubject.create();

  private final Map<Class<?>, Object> sessionHandlers = new HashMap<>();

  ManagedConnect(Connect connect) {
    this.connect = connect;
    connect.setOnCloseListener(c -> closeEvents.onNext(this));
  }

  public Observable<ManagedConnect> getCloseEvents() {
    return closeEvents;
  }

  public void registerSessionHandler(Object sessionHandler) {
    sessionHandlers.put(sessionHandler.getClass(), sessionHandler);
    if (sessionHandler instanceof SessionHandler) {
      ((SessionHandler) sessionHandler).bindConnect(this);
    }
  }

  public <T> T getSessionHandler(Class<T> clazz) {
    return (T) sessionHandlers.get(clazz);
  }

}
