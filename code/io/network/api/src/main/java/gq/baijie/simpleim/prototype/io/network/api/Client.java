package gq.baijie.simpleim.prototype.io.network.api;

public interface Client {

  void connect(String host, String port);

  void disconnect();

  <T> T getService(Class<T> clazz);

}
