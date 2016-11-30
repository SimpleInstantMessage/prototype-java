package gq.baijie.simpleim.prototype.impl.vertx.codec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import gq.baijie.simpleim.prototype.business.common.Message;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.parsetools.RecordParser;

public class MessageCodec implements RecordDataCodec {

  static final byte RECORD_TYPE = 3;
  private final Logger logger = LoggerFactory.getLogger(MessageCodec.class);

  @Override
  public List<Byte> supportDecodeRecordTypes() {
    return Collections.singletonList(RECORD_TYPE);
  }

  @Override
  public List<Class> supportEncodeRecordTypes() {
    return Collections.singletonList(Message.class);
  }

  @Override
  public Object decodeRecordData(byte recordType, Buffer recordData) {
    if (recordType == RECORD_TYPE) {
      return decodeMessage(recordData);
    } else {
      logger.error("unknown recordType: {}", recordType, new IllegalStateException());
      return null;
    }
  }

  @Override
  public Buffer encodeToRecordData(Object data) {
    if (Message.class.equals(data.getClass())) {
      return encodeMessage((Message) data);
    } else {
      logger.error("cannot encode record data: {}", data, new IllegalStateException());
      return Buffer.buffer(0);
    }
  }

  // UTF-8 doesn't contain FIELD_DELIMITER
  private static final Buffer FIELD_DELIMITER = Buffer.buffer(1).appendByte((byte) 0b1100_0000);

  private static Buffer encodeMessage(Message message) {
    // Write strings in UTF-8 encoding
    final Buffer buffer = Buffer.buffer();
    message.getReceivers()
        .forEach(r -> buffer.appendString(r.getReceiverId()).appendBuffer(FIELD_DELIMITER));
    buffer.appendString(message.getSenderId()).appendBuffer(FIELD_DELIMITER);
    buffer.appendString(message.getMessage()).appendBuffer(FIELD_DELIMITER);
    return buffer;
  }

  private static Message decodeMessage(Buffer messageRecord) {
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
