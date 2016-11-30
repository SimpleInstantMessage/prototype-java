package gq.baijie.simpleim.prototype.client.javafx;

import gq.baijie.simpleim.prototype.client.javafx.ui.JavafxApplication;

public class Main {

  public static final Main INSTANCE = new Main();

  public ClientComponent clientComponent = DaggerClientComponent.create();

  public static void main(String[] args) {
    JavafxApplication.launch(JavafxApplication.class, args);
    INSTANCE.clientComponent.getApplicationService().close();
  }

}
