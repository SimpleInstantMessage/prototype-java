package gq.baijie.simpleim.prototype.client.javafx.ui.login;

import gq.baijie.simpleim.prototype.client.javafx.Main;
import gq.baijie.simpleim.prototype.business.common.AccountService;
import gq.baijie.simpleim.prototype.business.client.SessionService;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class Controller {

  private final AccountService accountService;
  private final SessionService sessionService;

  @FXML
  private Label errorMessageOutput;

  @FXML
  private TextField accountIdInput;

  @FXML
  private PasswordField passwordInput;

  public Controller() {
    accountService = Main.INSTANCE.clientComponent.getAccountService();
    sessionService = Main.INSTANCE.clientComponent.getSessionService();
  }

  @FXML
  private void handleRegister() {
    final String accountId = accountIdInput.getText();
    final String password = passwordInput.getText();
    if (validateInput(accountId, password)) {
      AccountService.RegisterResult registerResult = accountService.register(accountId, password);
      if (registerResult != AccountService.RegisterResult.SUCCESS) {
        errorMessageOutput.setText(registerResult.toString());
        errorMessageOutput.setVisible(true);
      } else {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Register Result");
        alert.setHeaderText("Congratulations");
        alert.setContentText("Register successfully!");

        alert.showAndWait();
      }
    }
  }

  @FXML
  private void handleLogin() {
    final String accountId = accountIdInput.getText();
    final String password = passwordInput.getText();
    if (validateInput(accountId, password)) {
      final AccountService.LoginResult loginResult = sessionService.login(accountId, password);
      if (loginResult != AccountService.LoginResult.SUCCESS) {
        errorMessageOutput.setText(loginResult.toString());
        errorMessageOutput.setVisible(true);
      }
    }
  }

  private boolean validateInput(String accountId, String password) {
    String errorMessage = "";
    if (accountId.isEmpty()) {
      errorMessage += "Account ID is required\n";
    }
    if (password.isEmpty()) {
      errorMessage += "Password is required\n";
    }
    if (errorMessage.isEmpty()) {
      errorMessageOutput.setVisible(false);
      return true;
    } else {
      errorMessageOutput.setText(errorMessage);
      errorMessageOutput.setVisible(true);
      return false;
    }
  }

}
