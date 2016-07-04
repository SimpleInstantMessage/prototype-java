package gq.baijie.simpleim.prototype.server.service;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class ThreadService {

  private final ExecutorService mainExecutorService = Executors.newSingleThreadExecutor();

  @Inject
  public ThreadService() {}

  public ExecutorService getMainExecutorService() {
    return mainExecutorService;
  }

}
