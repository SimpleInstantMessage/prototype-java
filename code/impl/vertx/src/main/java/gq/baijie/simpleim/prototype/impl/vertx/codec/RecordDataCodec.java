package gq.baijie.simpleim.prototype.impl.vertx.codec;

import java.util.List;

import io.vertx.core.buffer.Buffer;

public interface RecordDataCodec {

  List<Byte> supportDecodeRecordTypes();

  List<Class> supportEncodeRecordTypes();

  Object decodeRecordData(byte recordType, Buffer recordData);

  Buffer encodeToRecordData(Object data);

}
