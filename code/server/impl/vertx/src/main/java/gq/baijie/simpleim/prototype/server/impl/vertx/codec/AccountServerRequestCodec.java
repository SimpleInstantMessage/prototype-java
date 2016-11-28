package gq.baijie.simpleim.prototype.server.impl.vertx.codec;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import gq.baijie.simpleim.prototype.server.service.AccountServerHandle.LoginRequestParameters;
import gq.baijie.simpleim.prototype.server.service.AccountServerHandle.LogoutRequestParameters;
import gq.baijie.simpleim.prototype.server.service.AccountServerHandle.RegisterRequestParameters;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.parsetools.RecordParser;

public class AccountServerRequestCodec implements RecordDataCodec {

  // UTF-8 doesn't contain FIELD_DELIMITER
  private static final Buffer FIELD_DELIMITER = Buffer.buffer(1).appendByte((byte) 0b1100_0000);

  private static final byte RECORD_TYPE_REQUEST = 1;

  private final Logger logger = LoggerFactory.getLogger(AccountServerRequestCodec.class);

  @Override
  public List<Byte> supportDecodeRecordTypes() {
    return Collections.singletonList(RECORD_TYPE_REQUEST);
  }

  @Override
  public List<Class> supportEncodeRecordTypes() {
    return Collections.singletonList(AccountServerRequest.class);
  }

  @Override
  public Object decodeRecordData(byte recordType, Buffer recordData) {
    switch (recordType) {
      case RECORD_TYPE_REQUEST:
        return decodeRequestRecordData(recordData);
      default:
        logger.error("unknown recordType: {}", recordType, new IllegalStateException());
        return null;
    }
  }

  @Override
  public Buffer encodeToRecordData(Object data) {
    if (AccountServerRequest.class.equals(data.getClass())) {
      return encodeRequestToRecordData((AccountServerRequest) data);
    } else {
      logger.error("cannot encode record data: {}", data, new IllegalStateException());
      return Buffer.buffer(0);
    }
  }

  private Buffer encodeRequestToRecordData(AccountServerRequest request) {
    Buffer requestData = null;
    if (request.data == null) {
      requestData = encodeToGetOnlineUsersRequestData();
    } else if (RegisterRequestParameters.class.equals(request.data.getClass())) {
      requestData = encodeToRegisterRequestData((RegisterRequestParameters) request.data);
    } else if (LoginRequestParameters.class.equals(request.data.getClass())) {
      requestData = encodeToLoginRequestData((LoginRequestParameters) request.data);
    } else if (LogoutRequestParameters.class.equals(request.data.getClass())) {
      requestData = encodeToLogoutRequestData((LogoutRequestParameters) request.data);
    }
    if (requestData != null) {
      return Buffer.buffer().appendByte(request.type).appendBuffer(requestData);
    } else {
      logger.error("cannot encode request: {}", request, new IllegalStateException());
      return Buffer.buffer(0);
    }
  }

  private AccountServerRequest decodeRequestRecordData(Buffer recordData) {
    byte requestType = recordData.getByte(0);
    Buffer requestData = recordData.getBuffer(1, recordData.length());
    Object decodedRequestData = recordData;
    switch (requestType) {
      case 1:
        decodedRequestData = decodeRegisterRequestData(requestData);
        break;
      case 2:
        decodedRequestData = decodeLoginRequestData(requestData);
        break;
      case 3:
        decodedRequestData = decodeLogoutRequestData(requestData);
        break;
      case 4:
        decodedRequestData = decodeGetOnlineUsersRequestData(requestData);
        break;
      default:
        logger.error("unknown requestType: {}", requestType, new IllegalStateException());
        break;
    }
    AccountServerRequest request = new AccountServerRequest();
    request.type = requestType;
    request.data = decodedRequestData;
    return request;
  }

  private Buffer encodeToRegisterRequestData(RegisterRequestParameters request) {
    final Buffer requestData = Buffer.buffer();
    requestData.appendString(request.accountId);
    requestData.appendBuffer(FIELD_DELIMITER);
    requestData.appendString(request.password);
    requestData.appendBuffer(FIELD_DELIMITER);
    return requestData;
  }

  private RegisterRequestParameters decodeRegisterRequestData(Buffer requestData) {
    LinkedList<String> fields = new LinkedList<>();
    RecordParser
        .newDelimited(FIELD_DELIMITER, field -> fields.add(field.toString()))
        .handle(requestData);
    final RegisterRequestParameters result = new RegisterRequestParameters();
    result.accountId = fields.get(0);
    result.password = fields.get(1);
    return result;
  }

  private Buffer encodeToLoginRequestData(LoginRequestParameters request) {
    final Buffer requestData = Buffer.buffer();
    requestData.appendString(request.accountId);
    requestData.appendBuffer(FIELD_DELIMITER);
    requestData.appendString(request.password);
    requestData.appendBuffer(FIELD_DELIMITER);
    return requestData;
  }

  private LoginRequestParameters decodeLoginRequestData(Buffer requestData) {
    LinkedList<String> fields = new LinkedList<>();
    RecordParser
        .newDelimited(FIELD_DELIMITER, field -> fields.add(field.toString()))
        .handle(requestData);
    final LoginRequestParameters result = new LoginRequestParameters();
    result.accountId = fields.get(0);
    result.password = fields.get(1);
    return result;
  }

  private Buffer encodeToLogoutRequestData(LogoutRequestParameters request) {
    final Buffer requestData = Buffer.buffer();
    requestData.appendString(request.accountId);
    return requestData;
  }

  private LogoutRequestParameters decodeLogoutRequestData(Buffer requestData) {
    final LogoutRequestParameters result = new LogoutRequestParameters();
    result.accountId = requestData.toString();
    return result;
  }

  private Buffer encodeToGetOnlineUsersRequestData() {
    return Buffer.buffer(0);
  }

  private Void decodeGetOnlineUsersRequestData(Buffer requestData) {
    return null;
  }

}
