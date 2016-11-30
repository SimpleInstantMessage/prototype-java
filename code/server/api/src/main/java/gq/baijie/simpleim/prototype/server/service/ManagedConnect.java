package gq.baijie.simpleim.prototype.server.service;

import java.util.HashMap;
import java.util.Map;

import gq.baijie.simpleim.prototype.server.service.Server.Connect;

public class ManagedConnect {

  private final Connect connect;

  private final Map<Class<?>, Object> handleServers = new HashMap<>();

  ManagedConnect(Connect connect) {
    this.connect = connect;
  }

  public void registerHandleServer(Object handleServer) {
    handleServers.put(handleServer.getClass(), handleServer);
  }

  public <T> T getHandleServer(Class<T> clazz) {
    return (T) handleServers.get(clazz);
  }

}
