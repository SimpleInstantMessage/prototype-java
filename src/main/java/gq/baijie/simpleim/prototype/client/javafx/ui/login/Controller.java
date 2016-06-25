package gq.baijie.simpleim.prototype.client.javafx.ui.login;

import javax.inject.Inject;

import gq.baijie.simpleim.prototype.client.javafx.Main;
import gq.baijie.simpleim.prototype.client.javafx.service.AccountService;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class Controller {

  @Inject
  private final AccountService accountService;

  @FXML
  private Label errorMessageOutput;

  @FXML
  private TextField accountIdInput;

  @FXML
  private PasswordField passwordInput;

  public Controller() {
    accountService = Main.INSTANCE.serviceComponent.getAccountService();
  }

  @FXML
  private void handleLogin() {
    String errorMessage = "";
    final String accountId = accountIdInput.getText();
    final String password = passwordInput.getText();
    if (accountId.isEmpty()) {
      errorMessage += "Account ID is required\n";
    }
    if (password.isEmpty()) {
      errorMessage += "Password is required\n";
    }
    if (errorMessage.isEmpty()) {
      accountService.login(accountId, password);
      errorMessageOutput.setVisible(false);
    } else {
      errorMessageOutput.setText(errorMessage);
      errorMessageOutput.setVisible(true);
    }
  }

}
