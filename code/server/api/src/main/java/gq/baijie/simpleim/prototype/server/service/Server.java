package gq.baijie.simpleim.prototype.server.service;

import java.util.function.Consumer;

import rx.Observable;

public interface Server {

  void start();

  void stop();

  Observable<Connect> connects();

  interface Connect {
    void setOnCloseListener(Consumer<Connect> listener);
    Observable<Object> handles();//TODO fire new handle multiple times?
  }

}
