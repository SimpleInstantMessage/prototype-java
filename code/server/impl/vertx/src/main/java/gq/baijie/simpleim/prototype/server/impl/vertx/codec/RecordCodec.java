package gq.baijie.simpleim.prototype.server.impl.vertx.codec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

import io.vertx.core.buffer.Buffer;

public class RecordCodec {

  private final Logger logger = LoggerFactory.getLogger(RecordCodec.class);

  private final Map<Class, RecordDataCodec> encoders = new HashMap<>();
  private final Map<Byte, RecordDataCodec> decoders = new HashMap<>();

  private static Buffer encodeToRecord(short id, byte type, Buffer data) {
    return Buffer.buffer().appendShort(id).appendByte(type).appendBuffer(data);
  }

  public void registerRecordDataCodec(RecordDataCodec codec) {
    codec.supportEncodeRecordTypes().forEach(type -> encoders.put(type, codec));
    codec.supportDecodeRecordTypes().forEach(type -> decoders.put(type, codec));
  }

  public Record decodeRecord(Buffer record) {
    Record result = new Record();
    result.id = record.getShort(0);
    result.type = record.getByte(2);
    Buffer recordData = record.getBuffer(3, record.length());
    final RecordDataCodec recordDataCodec = decoders.get(result.type);
    if (recordDataCodec != null) {
      result.data = recordDataCodec.decodeRecordData(result.type, recordData);
    } else {
      result.data = recordData;
    }
    return result;
  }

  public Buffer encodeToRecord(Record record) {
    RecordDataCodec codec = encoders.get(record.data.getClass());
    if (codec == null) {
      logger.error("unknown record data type", new IllegalStateException());
      return Buffer.buffer(0);
    }
    return encodeToRecord(record.id, record.type, codec.encodeToRecordData(record.data));
  }

}
