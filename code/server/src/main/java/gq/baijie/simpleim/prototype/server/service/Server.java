package gq.baijie.simpleim.prototype.server.service;

import rx.Observable;

public interface Server {

  void start();

  void stop();

  Observable<NewConnectEvent> connects();

  interface NewConnectEvent {
    Observable<Object> handles();
  }

}
