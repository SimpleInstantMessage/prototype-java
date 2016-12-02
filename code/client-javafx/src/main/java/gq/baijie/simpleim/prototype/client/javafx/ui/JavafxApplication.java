package gq.baijie.simpleim.prototype.client.javafx.ui;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;

import gq.baijie.simpleim.prototype.business.client.AccountService;
import gq.baijie.simpleim.prototype.business.client.SessionService;
import gq.baijie.simpleim.prototype.client.javafx.Main;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import rx.schedulers.JavaFxScheduler;

public class JavafxApplication extends Application {

  private final Logger logger = LoggerFactory.getLogger(JavafxApplication.class);

  Stage primaryStage;

  @Override
  public void start(Stage primaryStage) throws Exception {
    this.primaryStage = primaryStage;

    final SessionService sessionService = Main.INSTANCE.clientComponent.getSessionService();
    sessionService.loginStateEventBus()
        .observeOn(JavaFxScheduler.getInstance()).subscribe(state -> {
      logger.info("newState: {}", state);
      gotoSceneAccordingByLoginState(state);
    });
    gotoSceneAccordingByLoginState(sessionService.getLoginState());

    primaryStage.show();
  }

  private void gotoSceneAccordingByLoginState(AccountService.LoginState state) {
    switch (state) {
      case LOGGED_OUT:
        gotoLoginScene();
        break;
      case LOGGED_IN:
        gotoMainScene();
        break;
      default:
        throw new UnsupportedOperationException("unknown session state: " + state);
    }
  }

  private void gotoLoginScene() {
    gotoScene(getClass().getResource("login/login.fxml"));
  }

  private void gotoMainScene() {
    gotoScene(getClass().getResource("main/main.fxml"));
  }

  // Load root layout from fxml file.
  private void gotoScene(URL fxmlLocation) {
    FXMLLoader loader = new FXMLLoader();
    loader.setLocation(fxmlLocation);
    try {
      primaryStage.setScene(new Scene(loader.load()));
    } catch (IOException e) {
      e.printStackTrace();//TODO IOException
    }
  }

}
