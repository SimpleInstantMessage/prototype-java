package gq.baijie.simpleim.prototype.server.impl.vertx.codec;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import gq.baijie.simpleim.prototype.business.api.Message;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.parsetools.RecordParser;

public class MessageRecordCodec {

  // UTF-8 doesn't contain FIELD_DELIMITER
  private static final Buffer FIELD_DELIMITER = Buffer.buffer(1).appendByte((byte) 0b1100_0000);

  public static Buffer encodeMessage(Message message) {
    // Write strings in UTF-8 encoding
    final Buffer buffer = Buffer.buffer();
    message.getReceivers()
        .forEach(r -> buffer.appendString(r.getReceiverId()).appendBuffer(FIELD_DELIMITER));
    buffer.appendString(message.getSenderId()).appendBuffer(FIELD_DELIMITER);
    buffer.appendString(message.getMessage()).appendBuffer(FIELD_DELIMITER);
    return buffer;
  }

  public static Message decodeMessage(Buffer messageRecord) {
    LinkedList<String> fields = new LinkedList<>();
    RecordParser
        .newDelimited(FIELD_DELIMITER, field -> fields.add(field.toString()))
        .handle(messageRecord);

    final List<Message.Receiver> receivers = fields.stream()
        .limit(fields.size() - 2)
        .map(Message.Receiver::new)
        .collect(Collectors.toList());
    final String senderId = fields.get(fields.size()-2);
    final String message = fields.get(fields.size()-1);

    return new Message(senderId, receivers, message);
  }

}
