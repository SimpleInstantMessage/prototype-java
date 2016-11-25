package gq.baijie.simpleim.prototype.client.javafx.ui.main;

import gq.baijie.simpleim.prototype.client.javafx.Main;
import gq.baijie.simpleim.prototype.client.javafx.service.AccountService;
import gq.baijie.simpleim.prototype.client.javafx.service.SessionService;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;

public class Controller {

  private final AccountService accountService;
  private final SessionService sessionService;

  @FXML
  private Label accountId;
  @FXML
  private ListView<String> onlineUserList;

  public Controller() {
    accountService = Main.INSTANCE.serviceComponent.getAccountService();
    sessionService = Main.INSTANCE.serviceComponent.getSessionService();
  }

  @FXML
  private void initialize() {
    accountId.setText(sessionService.getAccountId());
    onlineUserList.getItems().setAll(accountService.onlineUsers());
  }

  @FXML
  private void handleLogout() {
    accountService.logout();
  }

}
