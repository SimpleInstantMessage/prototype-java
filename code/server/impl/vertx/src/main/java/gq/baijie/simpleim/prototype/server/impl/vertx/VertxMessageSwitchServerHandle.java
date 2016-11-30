package gq.baijie.simpleim.prototype.server.impl.vertx;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gq.baijie.simpleim.prototype.business.api.Message;
import gq.baijie.simpleim.prototype.server.impl.vertx.codec.Record;
import gq.baijie.simpleim.prototype.server.service.MessageSwitchServerHandle;

public class VertxMessageSwitchServerHandle implements MessageSwitchServerHandle {

  private final Logger logger = LoggerFactory.getLogger(VertxMessageSwitchServerHandle.class);

  private final NetSocketConnect connect;

  private final VertxAccountServerHandle accountServerHandle;

  private OnReceiveRequestListener requestListener;

  public VertxMessageSwitchServerHandle(NetSocketConnect connect,
                                        VertxAccountServerHandle accountServerHandle) {
    this.connect = connect;
    this.accountServerHandle = accountServerHandle;
    init();
  }

  private void init() {
    connect.records()
        .map(record -> record.data)
        .ofType(Message.class)
        .subscribe(this::onReceiveMessage);
  }

  private void onReceiveMessage(Message message) {
    if (accountServerHandle.getLoggedInAccountId() == null) {
      logger.warn("ignore onReceiveMessage because not logged in");
      return;
    }
    if (requestListener != null) {
      requestListener.onReceiveMessage(message);
    } else {
      logger.warn("ignore message because no requestListener");
    }
  }

  @Override
  public void setOnReceiveRequestListener(OnReceiveRequestListener listener) {
    requestListener = listener;
  }

  @Override
  public void sendMessage(Message message) {
    if (accountServerHandle.getLoggedInAccountId() == null) {
      logger.warn("ignore sendMessage because not logged in");
      return;
    }
    connect.writeRecord(Record.of(message));
  }

}
