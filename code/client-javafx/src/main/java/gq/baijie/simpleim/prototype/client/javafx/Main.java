package gq.baijie.simpleim.prototype.client.javafx;

import gq.baijie.simpleim.prototype.client.javafx.inject.DaggerServiceComponent;
import gq.baijie.simpleim.prototype.client.javafx.inject.ServiceComponent;
import gq.baijie.simpleim.prototype.client.javafx.ui.JavafxApplication;

public class Main {

  public static final Main INSTANCE = new Main();

  public ServiceComponent serviceComponent = DaggerServiceComponent.create();

  public static void main(String[] args) {
    JavafxApplication.launch(JavafxApplication.class, args);
  }

}
