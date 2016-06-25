package gq.baijie.simpleim.prototype.client.javafx.ui;

import gq.baijie.simpleim.prototype.client.javafx.Main;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class JavafxApplication extends Application {

  @Override
  public void start(Stage primaryStage) throws Exception {
    // Load root layout from fxml file.
    FXMLLoader loader = new FXMLLoader();
    loader.setLocation(getClass().getResource("login/login.fxml"));
    primaryStage.setScene(new Scene(loader.load()));
    primaryStage.show();

    Main.INSTANCE.serviceComponent.getSessionService().getStateChangeEvents().subscribe(event->{
      System.out.println("oldState: "+event.oldValue+", newState: "+event.newValue);
    });
  }

}
