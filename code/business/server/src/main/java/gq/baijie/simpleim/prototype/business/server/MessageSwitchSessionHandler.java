package gq.baijie.simpleim.prototype.business.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gq.baijie.simpleim.prototype.business.common.MessageSwitchService;

public class MessageSwitchSessionHandler implements SessionHandler {

  private final Logger logger = LoggerFactory.getLogger(MessageSwitchSessionHandler.class);

  private final MessageSwitchService.Session session;
  private final MessageSwitchSession handle;

  private ManagedConnect managedConnect;

  public MessageSwitchSessionHandler(MessageSwitchService messageSwitchService,
                                     MessageSwitchSession handle) {
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
    final AccountSessionHandler accountSessionHandler = managedConnect == null ? null :
                                                      managedConnect.getSessionHandler(AccountSessionHandler.class);
    return accountSessionHandler != null && accountSessionHandler.getLoggedInAccountId() != null;
  }

}
