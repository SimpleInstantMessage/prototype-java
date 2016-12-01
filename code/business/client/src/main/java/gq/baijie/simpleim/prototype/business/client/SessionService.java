package gq.baijie.simpleim.prototype.business.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.NotThreadSafe;
import javax.inject.Inject;
import javax.inject.Singleton;

import gq.baijie.simpleim.prototype.business.client.AccountService.LoginState;
import gq.baijie.simpleim.prototype.business.common.AccountService.LoginResult;
import gq.baijie.simpleim.prototype.business.common.Message;
import rx.Observable;

@Singleton
@NotThreadSafe
public class SessionService {

  final Logger logger = LoggerFactory.getLogger(SessionService.class);

  private final AccountService accountService;
  private final ChatService chatService;

  final Observable<Message> receiveMessageEventBus;
  ConversationService conversationService;
  String token;//TODO use this?

  @Inject
  public SessionService(AccountService accountService, ChatService chatService) {
    this.accountService = accountService;
    this.chatService = chatService;
    // bind AccountService
    bindAccountService();
    // bind ChatService
    receiveMessageEventBus = chatService.newMessageEventBus()
        .filter(m -> haveLoggedIn())
        .filter(m -> m.getReceivers().stream().anyMatch(r -> getLoggedInAccountId().equals(r.getReceiverId())));
    //TODO release receiveMessageEventBus (onFinish and unsubscribe)
    receiveMessageEventBus.subscribe(m -> {
      if (conversationService != null) {
        conversationService.logNewMessage(m);
      }
    });
  }

  private void bindAccountService() {
    accountService.loginStateEventBus().subscribe(state -> {
      if (state == LoginState.LOGGED_IN) {
        onLoggedIn();
      } else if (state == LoginState.LOGGED_OUT) {
        onLoggedOut();
      }
    });
  }

  private void onLoggedIn() {
    conversationService = new ConversationService();
  }

  private void onLoggedOut() {
    conversationService = null;
  }

  public ConversationService getConversationService() {
    return conversationService;
  }

  public LoginResult login(@Nonnull String accountId, @Nonnull String password) {
    return accountService.login(accountId, password);
  }

  public void logout() {
    accountService.logout();
  }

  public boolean haveLoggedIn() {
    return accountService.haveLoggedIn();
  }

  public LoginState getLoginState() {
    return accountService.getLoginState();
  }

  public Observable<LoginState> loginStateEventBus() {
    return accountService.loginStateEventBus();
  }

  public String getLoggedInAccountId() {
    return accountService.getLoggedInAccountId();
  }

  public Message sendMessage(String message, Set<String> receiverIds) {
    final String accountId = getLoggedInAccountId();
    if (accountId != null) {
      final Message sentMsg = chatService.sendMessage(accountId, message, receiverIds);
      conversationService.logNewMessage(sentMsg);
      return sentMsg;
    } else {
      logger.error("send message when haven't logged in", new IllegalStateException());
      return null;
    }
  }

}
