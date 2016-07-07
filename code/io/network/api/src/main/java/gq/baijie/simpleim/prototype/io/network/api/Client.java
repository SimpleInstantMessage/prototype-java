package gq.baijie.simpleim.prototype.io.network.api;

public interface Client {

  void connect(String host, int port);

  void disconnect();

  <T> T getService(Class<T> clazz);

}
