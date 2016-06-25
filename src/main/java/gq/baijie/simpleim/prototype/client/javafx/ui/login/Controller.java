package gq.baijie.simpleim.prototype.client.javafx.ui.login;

import javax.inject.Inject;

import gq.baijie.simpleim.prototype.client.javafx.Main;
import gq.baijie.simpleim.prototype.client.javafx.service.AccountService;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class Controller {

  @Inject
  private final AccountService accountService;

  @FXML
  private TextField accountIdInput;

  @FXML
  private PasswordField passwordInput;

  public Controller() {
    accountService = Main.INSTANCE.serviceComponent.getAccountService();
  }

  @FXML
  private void handleLogin() {
    accountService.login(accountIdInput.getText(), passwordInput.getText());
  }

}
