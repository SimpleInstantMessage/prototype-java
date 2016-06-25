package gq.baijie.simpleim.prototype.client.javafx.service;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.NotThreadSafe;
import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Observable;
import rx.subjects.PublishSubject;

@Singleton
@NotThreadSafe
public class SessionService {

  State state = State.LOGGED_OUT;
  private PublishSubject<ChangeEvent<State>> stateChangeEvents = PublishSubject.create();
  /* when state == LOGGED_IN */
  String accountId;
  String token;//TODO use this?

  @Inject
  public SessionService() {
  }

  public Observable<ChangeEvent<State>> getStateChangeEvents() {
    return stateChangeEvents.asObservable();
  }

  public void gotoHaveLoggedInState(@Nonnull String accountId) {//TODO token?
    this.accountId = accountId;
    changeState(State.LOGGED_IN);
  }

  public void gotoHaveLoggedOutState() {
    accountId = null;
    changeState(State.LOGGED_OUT);
  }

  private void changeState(State newState) {
    if (newState != state) {
      final State oldState = state;
      state = newState;
      stateChangeEvents.onNext(new ChangeEvent<>(oldState, newState));
    }
  }

  public boolean haveLoggedIn() {
    return state == State.LOGGED_IN;
  }

  public enum State {
    LOGGED_IN,
    LOGGED_OUT
  }

  public static class ChangeEvent<T> {
    public final T oldValue;
    public final T newValue;

    public ChangeEvent(T oldValue, T newValue) {
      this.oldValue = oldValue;
      this.newValue = newValue;
    }
  }

}
