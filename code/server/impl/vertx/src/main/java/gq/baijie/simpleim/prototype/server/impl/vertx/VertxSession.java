package gq.baijie.simpleim.prototype.server.impl.vertx;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import gq.baijie.simpleim.prototype.business.api.Message;
import gq.baijie.simpleim.prototype.business.api.MessageSwitchService;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetSocket;
import io.vertx.core.parsetools.RecordParser;
import rx.Observable;
import rx.subjects.PublishSubject;

class VertxSession implements MessageSwitchService.Session {
  // UTF-8 doesn't contain FIELD_DELIMITER and RECORD_DELIMITER
  private static final Buffer FIELD_DELIMITER = Buffer.buffer(1).appendByte((byte) 0b1100_0000);
  private static final Buffer RECORD_DELIMITER = Buffer.buffer(1).appendByte((byte) 0b1100_0001);

  private final PublishSubject<Message> receiveMessages = PublishSubject.create();

  private final NetSocket socket;

  VertxSession(NetSocket socket) {
    this.socket = socket;
    socket.handler(RecordParser.newDelimited(RECORD_DELIMITER, buffer -> {
      LinkedList<String> fields = new LinkedList<>();
      RecordParser.newDelimited(FIELD_DELIMITER, field -> fields.add(field.toString())).handle(buffer);

      final List<Message.Receiver> receivers = fields.stream()
          .limit(fields.size() - 2)
          .map(Message.Receiver::new)
          .collect(Collectors.toList());
      final String senderId = fields.get(fields.size()-2);
      final String message = fields.get(fields.size()-1);

      receiveMessages.onNext(new Message(senderId, receivers, message));
    }));
  }

  @Override
  public void sendMessage(Message message) {
    // Write strings in UTF-8 encoding
    final Buffer buffer = Buffer.buffer();
    message.getReceivers().forEach(
        r -> buffer.appendString(r.getReceiverId()).appendBuffer(FIELD_DELIMITER));
    buffer.appendString(message.getSenderId()).appendBuffer(FIELD_DELIMITER);
    buffer.appendString(message.getMessage()).appendBuffer(FIELD_DELIMITER);
    buffer.appendBuffer(RECORD_DELIMITER);
    socket.write(buffer);
  }

  @Override
  public Observable<Message> receiveMessages() {
    return receiveMessages;
  }

  @Override
  public void close() {
    socket.close();
  }

}
