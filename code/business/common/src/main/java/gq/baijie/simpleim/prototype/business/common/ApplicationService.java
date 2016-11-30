package gq.baijie.simpleim.prototype.business.common;

import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Observable;
import rx.subjects.PublishSubject;

@Singleton
public class ApplicationService {

  private final PublishSubject<Void> closeEventBus = PublishSubject.create();

  @Inject
  public ApplicationService() {
  }

  public void close() {
    closeEventBus.onNext(null);
  }

  public Observable<Void> closeEventBus() {
    return closeEventBus;
  }

}
