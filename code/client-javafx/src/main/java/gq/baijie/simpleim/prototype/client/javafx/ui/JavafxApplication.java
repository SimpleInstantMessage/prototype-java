package gq.baijie.simpleim.prototype.client.javafx.ui;

import java.io.IOException;
import java.net.URL;

import gq.baijie.simpleim.prototype.client.javafx.Main;
import gq.baijie.simpleim.prototype.business.client.SessionService;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class JavafxApplication extends Application {

  Stage primaryStage;

  @Override
  public void start(Stage primaryStage) throws Exception {
    this.primaryStage = primaryStage;

    final SessionService sessionService = Main.INSTANCE.clientComponent.getSessionService();
    sessionService.getStateChangeEvents().subscribe(event->{
      System.out.println("oldState: "+event.oldValue+", newState: "+event.newValue);
      gotoSceneAccroddingBySessionState(event.newValue);
    });
    gotoSceneAccroddingBySessionState(sessionService.getState());

    primaryStage.show();
  }

  private void gotoSceneAccroddingBySessionState(SessionService.State state) {
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
