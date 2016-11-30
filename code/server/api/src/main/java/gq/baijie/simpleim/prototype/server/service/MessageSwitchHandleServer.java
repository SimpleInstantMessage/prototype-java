package gq.baijie.simpleim.prototype.server.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gq.baijie.simpleim.prototype.business.api.MessageSwitchService;

public class MessageSwitchHandleServer implements HandleServer {

  private final Logger logger = LoggerFactory.getLogger(MessageSwitchHandleServer.class);

  private final MessageSwitchService.Session session;
  private final MessageSwitchServerHandle handle;

  private ManagedConnect managedConnect;

  public MessageSwitchHandleServer(MessageSwitchService messageSwitchService,
                                   MessageSwitchServerHandle handle) {
    session = messageSwitchService.connect();
    this.handle = handle;
  }

  @Override
  public void bindConnect(ManagedConnect connect) {
    managedConnect = connect;
    init();
  }

  private void init() {
    session.receiveMessages().subscribe(message -> {
      if (hasLoggedIn()) {
        handle.sendMessage(message);
      } else {
        logger.warn("ignore sendMessage because not logged in");
      }
    });
    handle.setOnReceiveRequestListener(message -> {
      if (hasLoggedIn()) {
        session.sendMessage(message);
      } else {
        logger.warn("ignore onReceiveMessage because not logged in");
      }
    });
  }

  private boolean hasLoggedIn() {
    final AccountHandleServer accountHandleServer = managedConnect == null ? null :
        managedConnect.getHandleServer(AccountHandleServer.class);
    return accountHandleServer != null && accountHandleServer.getLoggedInAccountId() != null;
  }

}
