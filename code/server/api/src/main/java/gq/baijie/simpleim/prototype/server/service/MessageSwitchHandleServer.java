package gq.baijie.simpleim.prototype.server.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gq.baijie.simpleim.prototype.business.api.MessageSwitchService;

public class MessageSwitchHandleServer {

  private final Logger logger = LoggerFactory.getLogger(MessageSwitchHandleServer.class);


  private final MessageSwitchService messageSwitchService;

  private final MessageSwitchService.Session session;

  private final ManagedConnect managedConnect;

  public MessageSwitchHandleServer(MessageSwitchService messageSwitchService,
                                   ManagedConnect managedConnect,
                                   MessageSwitchServerHandle handle) {
    this.messageSwitchService = messageSwitchService;
    session = messageSwitchService.connect();
    this.managedConnect = managedConnect;
    init(handle);
  }

  private void init(MessageSwitchServerHandle handle) {
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
    final AccountHandleServer accountHandleServer =
        managedConnect.getHandleServer(AccountHandleServer.class);
    return accountHandleServer != null && accountHandleServer.getLoggedInAccountId() != null;
  }

}
