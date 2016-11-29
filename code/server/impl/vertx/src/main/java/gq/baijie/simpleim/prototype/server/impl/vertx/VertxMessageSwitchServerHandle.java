package gq.baijie.simpleim.prototype.server.impl.vertx;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gq.baijie.simpleim.prototype.business.api.Message;
import gq.baijie.simpleim.prototype.server.impl.vertx.codec.Record;
import gq.baijie.simpleim.prototype.server.service.MessageSwitchServerHandle;

public class VertxMessageSwitchServerHandle implements MessageSwitchServerHandle {

  private final Logger logger = LoggerFactory.getLogger(VertxMessageSwitchServerHandle.class);

  private final NetSocketConnect connect;

  private OnReceiveRequestListener requestListener;

  public VertxMessageSwitchServerHandle(NetSocketConnect connect) {
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
