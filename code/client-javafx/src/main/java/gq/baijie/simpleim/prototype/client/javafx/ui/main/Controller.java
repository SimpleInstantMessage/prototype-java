package gq.baijie.simpleim.prototype.client.javafx.ui.main;

import java.util.Collections;

import gq.baijie.simpleim.prototype.client.javafx.Main;
import gq.baijie.simpleim.prototype.client.javafx.service.AccountService;
import gq.baijie.simpleim.prototype.client.javafx.service.ConversationService;
import gq.baijie.simpleim.prototype.client.javafx.service.SessionService;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;

public class Controller {

  private final AccountService accountService;
  private final SessionService sessionService;
  private final ConversationService conversationService;

  @FXML
  private Label accountId;
  @FXML
  private ListView<ConversationService.Conversation> onlineUserList;

  public Controller() {
    accountService = Main.INSTANCE.serviceComponent.getAccountService();
    sessionService = Main.INSTANCE.serviceComponent.getSessionService();
    conversationService = sessionService.getConversationService();
  }

  @FXML
  private void initialize() {
    accountId.setText(sessionService.getAccountId());
//    onlineUserList.getItems().setAll(accountService.onlineUsers());
    accountService.onlineUsers().forEach(
        u -> conversationService.touchConversation(Collections.singleton(u)));
    onlineUserList.setCellFactory(listView -> new ConversationCell());
    onlineUserList.setItems(conversationService.getConversations());
  }

  @FXML
  private void handleLogout() {
    accountService.logout();
  }

  private static class ConversationCell extends ListCell<ConversationService.Conversation> {

    @Override
    protected void updateItem(ConversationService.Conversation item, boolean empty) {
      super.updateItem(item, empty);
      setText(item == null ? "" : String.join(", ", item.getParticipantIds()));
    }
  }
}
