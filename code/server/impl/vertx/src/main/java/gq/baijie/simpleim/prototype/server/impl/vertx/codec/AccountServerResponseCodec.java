package gq.baijie.simpleim.prototype.server.impl.vertx.codec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;

import io.vertx.core.buffer.Buffer;

public class AccountServerResponseCodec implements RecordDataCodec {

  private static final byte RECORD_TYPE_RESPONSE = 2;
  private final Logger logger = LoggerFactory.getLogger(AccountServerResponseCodec.class);

  @Override
  public List<Byte> supportDecodeRecordTypes() {
    return Collections.singletonList(RECORD_TYPE_RESPONSE);
  }

  @Override
  public List<Class> supportEncodeRecordTypes() {
    return Collections.singletonList(AccountServerResponse.class);
  }

  @Override
  public Object decodeRecordData(byte recordType, Buffer recordData) {
    if (recordType == RECORD_TYPE_RESPONSE) {
      return decodeResponseRecordData(recordData);
    } else {
      logger.error("unknown recordType: {}", recordType, new IllegalStateException());
      return null;
    }
  }

  @Override
  public Buffer encodeToRecordData(Object data) {
    if (AccountServerResponse.class.equals(data.getClass())) {
      return encodeResponseToRecordData((AccountServerResponse) data);
    } else {
      logger.error("cannot encode record data: {}", data, new IllegalStateException());
      return Buffer.buffer(0);
    }
  }

  private Buffer encodeResponseToRecordData(AccountServerResponse response) {
    return Buffer.buffer().appendShort(response.requestId).appendBuffer(response.data);
  }

  private AccountServerResponse decodeResponseRecordData(Buffer recordData) {
    AccountServerResponse response = new AccountServerResponse();
    response.requestId = recordData.getShort(0);
    response.data = recordData.getBuffer(2, recordData.length());
    return response;
  }
}
