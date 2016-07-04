package gq.baijie.simpleim.prototype.server.service;

import java.util.concurrent.CopyOnWriteArrayList;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class SystemManagerService {

  private final ThreadService threadService;

  private final CopyOnWriteArrayList<Runnable> startTaskList = new CopyOnWriteArrayList<>();
  private final CopyOnWriteArrayList<Runnable> stopTasksList = new CopyOnWriteArrayList<>();

  @Inject
  public SystemManagerService(ThreadService threadService) {
    this.threadService = threadService;
  }

  /**
   * Run {@link #getStartTaskList()} in {@link ThreadService#getMainExecutorService()}
   */
  //TODO shouldn't start twice
  public void start() {
    threadService.getMainExecutorService().submit(()->startTaskList.forEach(Runnable::run));
  }

  /**
   * Run {@link #getStopTasksList()} in {@link ThreadService#getMainExecutorService()}
   */
  public void stop() {
    threadService.getMainExecutorService().submit(()->stopTasksList.forEach(Runnable::run));
  }

  public CopyOnWriteArrayList<Runnable> getStartTaskList() {
    return startTaskList;
  }

  public CopyOnWriteArrayList<Runnable> getStopTasksList() {
    return stopTasksList;
  }

}
