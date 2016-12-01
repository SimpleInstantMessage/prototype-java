package gq.baijie.simpleim.prototype.business.server;

import java.util.function.Consumer;

import rx.Observable;

public interface Server {

  void start();

  void stop();

  Observable<Connect> connects();

  interface Connect {
    void setOnCloseListener(Consumer<Connect> listener);
    Observable<Session> sessions();//TODO fire new session multiple times?

    interface Session {}
  }

}
