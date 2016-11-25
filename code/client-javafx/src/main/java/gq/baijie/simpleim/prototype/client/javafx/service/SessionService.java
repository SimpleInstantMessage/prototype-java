package gq.baijie.simpleim.prototype.client.javafx.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.NotThreadSafe;
import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Observable;
import rx.subjects.PublishSubject;

@Singleton
@NotThreadSafe
public class SessionService {

  final Logger logger = LoggerFactory.getLogger(SessionService.class);

  @Inject
  ChatService chatService;

  State state = State.LOGGED_OUT;
  private PublishSubject<ChangeEvent<State>> stateChangeEvents = PublishSubject.create();
  /* when state == LOGGED_IN */
  String accountId;
  Observable<ChatService.Message> receiveMessageEventBus;
  ConversationService conversationService;
  String token;//TODO use this?

  @Inject
  public SessionService() {
  }

  public State getState() {
    return state;
  }

  public String getAccountId() {
    return accountId;
  }

  public Observable<ChangeEvent<State>> getStateChangeEvents() {
    return stateChangeEvents.asObservable();
  }

  public ConversationService getConversationService() {
    return conversationService;
  }

  public void gotoHaveLoggedInState(@Nonnull String accountId) {//TODO token?
    this.accountId = accountId;
    conversationService = new ConversationService();
    receiveMessageEventBus = chatService.newMessageEventBus().filter(m -> m.getReceivers().stream()
        .anyMatch(receiver -> accountId.equals(receiver.getReceiverId())));
    receiveMessageEventBus.subscribe(m -> conversationService.logNewMessage(m));
    changeState(State.LOGGED_IN);
  }

  public void gotoHaveLoggedOutState() {
    //TODO release receiveMessageEventBus
    receiveMessageEventBus = null;
    conversationService = null;
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

  public ChatService.Message sendMessage(String message, Set<String> receiverIds) {
    final String accountId = getAccountId();
    if (accountId != null) {
      return chatService.sendMessage(accountId, message, receiverIds);
    } else {
      logger.error("send message when haven't logged in", new IllegalStateException());
      return null;
    }
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
