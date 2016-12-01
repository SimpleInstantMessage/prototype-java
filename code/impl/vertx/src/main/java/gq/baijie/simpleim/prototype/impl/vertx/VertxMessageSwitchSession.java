package gq.baijie.simpleim.prototype.impl.vertx;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gq.baijie.simpleim.prototype.business.common.Message;
import gq.baijie.simpleim.prototype.impl.vertx.codec.Record;
import gq.baijie.simpleim.prototype.business.server.MessageSwitchSession;

public class VertxMessageSwitchSession implements MessageSwitchSession {

  private final Logger logger = LoggerFactory.getLogger(VertxMessageSwitchSession.class);

  private final NetSocketConnect connect;

  private OnReceiveRequestListener requestListener;

  public VertxMessageSwitchSession(NetSocketConnect connect) {
    this.connect = connect;
    init();
  }

  private void init() {
    connect.records()
        .map(record -> record.data)
        .ofType(Message.class)
        .subscribe(this::onReceiveMessage);
  }

  private void onReceiveMessage(Message message) {
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
    connect.writeRecord(Record.of(message));
  }

}
