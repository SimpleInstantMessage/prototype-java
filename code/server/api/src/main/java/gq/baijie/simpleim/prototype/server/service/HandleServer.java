package gq.baijie.simpleim.prototype.server.service;

import javax.inject.Inject;

public class HandleServer {

  @Inject
  MessageSwitchServer messageSwitchServer;
  @Inject
  AccountServer accountServer;

  @Inject
  public HandleServer() {
  }

  public void onNewHandle(Object handle) {
    if (handle instanceof MessageSwitchServerHandle) {
      messageSwitchServer.onReceiveHandler((MessageSwitchServerHandle) handle);
    }
    if (handle instanceof AccountServerHandle) {
      accountServer.onReceiveHandler((AccountServerHandle) handle);
    }
  }

}
