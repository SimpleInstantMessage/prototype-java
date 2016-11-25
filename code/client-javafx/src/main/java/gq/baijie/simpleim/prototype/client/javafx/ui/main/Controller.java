package gq.baijie.simpleim.prototype.client.javafx.ui.main;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.stream.Collectors;

import gq.baijie.simpleim.prototype.client.javafx.Main;
import gq.baijie.simpleim.prototype.client.javafx.service.AccountService;
import gq.baijie.simpleim.prototype.client.javafx.service.ChatService;
import gq.baijie.simpleim.prototype.client.javafx.service.ConversationService;
import gq.baijie.simpleim.prototype.client.javafx.service.SessionService;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import rx.Subscription;

public class Controller {

  private final Logger logger = LoggerFactory.getLogger(Controller.class);

  private final AccountService accountService;
  private final SessionService sessionService;
  private final ConversationService conversationService;

  private ConversationService.Conversation currentConversation;
  private Subscription conversationSubscription;

  @FXML
  private Label accountId;
  @FXML
  private ListView<ConversationService.Conversation> onlineUserList;
  @FXML
  private TextArea conversationLog;
  @FXML
  private TextArea inputMessage;

  public Controller() {
    accountService = Main.INSTANCE.serviceComponent.getAccountService();
    sessionService = Main.INSTANCE.serviceComponent.getSessionService();
    conversationService = sessionService.getConversationService();
  }

  @Override
  protected void finalize() throws Throwable {
    unbindConversation();
    super.finalize();
  }

  @FXML
  private void initialize() {
    accountId.setText(sessionService.getAccountId());
//    onlineUserList.getItems().setAll(accountService.onlineUsers());
    accountService.onlineUsers().forEach(
        u -> conversationService.touchConversation(Collections.singleton(u)));
    onlineUserList.setCellFactory(listView -> new ConversationCell());
    onlineUserList.setItems(conversationService.getConversations());
    onlineUserList.getSelectionModel().selectedItemProperty().addListener((o, oldV, newV) -> {
      currentConversation = newV;
      bindConversation(newV);
    });
  }

  private void bindConversation(ConversationService.Conversation conversation) {
    unbindConversation();
    conversationLog.setText("");
    conversationSubscription = conversation.getAddNewMessageEvents().subscribe(m -> {
      conversationLog.appendText("\n" + m.getMessage());//TODO improve this
    });
    conversationLog.setText(conversation.getMessages().stream()
                                .map(ChatService.Message::getMessage)
                                .collect(Collectors.joining("\n")));
  }
  private void unbindConversation() {
    if (conversationSubscription != null) {
      conversationSubscription.unsubscribe();
      conversationSubscription = null;
    }
  }

  @FXML
  private void handleLogout() {
    accountService.logout();
  }

  @FXML
  private void handleSendMessage() {
    if (currentConversation == null) {
      logger.error("send message when no currentConversation", new IllegalStateException());
      return;
    }
    sessionService.sendMessage(inputMessage.getText(), currentConversation.getParticipantIds());
  }

  private static class ConversationCell extends ListCell<ConversationService.Conversation> {

    @Override
    protected void updateItem(ConversationService.Conversation item, boolean empty) {
      super.updateItem(item, empty);
      setText(item == null ? "" : String.join(", ", item.getParticipantIds()));
    }
  }
}
