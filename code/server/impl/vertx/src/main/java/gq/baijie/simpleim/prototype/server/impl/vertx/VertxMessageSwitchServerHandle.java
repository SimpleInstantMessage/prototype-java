package gq.baijie.simpleim.prototype.server.impl.vertx;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gq.baijie.simpleim.prototype.business.api.Message;
import gq.baijie.simpleim.prototype.server.impl.vertx.codec.MessageRecordCodec;
import gq.baijie.simpleim.prototype.server.service.MessageSwitchServerHandle;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetSocket;
import io.vertx.core.parsetools.RecordParser;

public class VertxMessageSwitchServerHandle implements MessageSwitchServerHandle {
  // UTF-8 doesn't contain RECORD_DELIMITER
  private static final Buffer RECORD_DELIMITER = Buffer.buffer(1).appendByte((byte) 0b1100_0001);

  private final Logger logger = LoggerFactory.getLogger(VertxMessageSwitchServerHandle.class);

  private final NetSocket socket;

  private OnReceiveRequestListener requestListener;

  public VertxMessageSwitchServerHandle(NetSocket socket) {
    this.socket = socket;
    socket.handler(RecordParser.newDelimited(RECORD_DELIMITER, this::onReceiveRecord));
  }

  private void onReceiveRecord(Buffer record) {
    onReceiveMessage(MessageRecordCodec.decodeMessage(record));
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
    final Buffer buffer = MessageRecordCodec.encodeMessage(message);
    buffer.appendBuffer(RECORD_DELIMITER);
    socket.write(buffer);
  }

}
