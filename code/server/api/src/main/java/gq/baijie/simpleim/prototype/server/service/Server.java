package gq.baijie.simpleim.prototype.server.service;

import rx.Observable;

public interface Server {

  void start();

  void stop();

  Observable<Connect> connects();

  interface Connect {
    Observable<Object> handles();//TODO fire new handle multiple times?
  }

}
