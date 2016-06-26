package gq.baijie.simpleim.prototype.client.javafx.ui.main;

import gq.baijie.simpleim.prototype.client.javafx.Main;
import gq.baijie.simpleim.prototype.client.javafx.service.AccountService;
import javafx.fxml.FXML;

public class Controller {

  private final AccountService accountService;

  public Controller() {
    accountService = Main.INSTANCE.serviceComponent.getAccountService();
  }

  @FXML
  private void handleLogout() {
    accountService.logout();
  }

}
