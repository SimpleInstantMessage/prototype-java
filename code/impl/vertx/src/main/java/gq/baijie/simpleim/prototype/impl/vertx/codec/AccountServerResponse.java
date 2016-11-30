package gq.baijie.simpleim.prototype.impl.vertx.codec;

import java.util.LinkedList;
import java.util.List;

import gq.baijie.simpleim.prototype.business.common.AccountService.LoginResult;
import gq.baijie.simpleim.prototype.business.common.AccountService.RegisterResult;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.parsetools.RecordParser;

public class AccountServerResponse {

  // UTF-8 doesn't contain FIELD_DELIMITER
  private static final Buffer FIELD_DELIMITER = Buffer.buffer(1).appendByte((byte) 0b1100_0000);

  public short requestId;

  public Buffer data;

  private static AccountServerResponse of(short requestId, Buffer data) {
    AccountServerResponse result = new AccountServerResponse();
    result.requestId = requestId;
    result.data = data;
    return result;
  }

  public static AccountServerResponse of(short requestId, RegisterResult result) {
    return of(requestId, Buffer.buffer(result.name()));
  }

  public static AccountServerResponse of(short requestId, LoginResult result) {
    return of(requestId, Buffer.buffer(result.name()));
  }

  public static AccountServerResponse of(short requestId, Void result) {
    return of(requestId, Buffer.buffer(0));
  }

  public static AccountServerResponse of(short requestId, List<String> onlineUsers) {
    Buffer buffer = Buffer.buffer();
    onlineUsers.forEach(user -> buffer.appendString(user).appendBuffer(FIELD_DELIMITER));
    return of(requestId, buffer);
  }

  public RegisterResult toRegisterResult() {
    return RegisterResult.valueOf(data.toString());
  }

  public LoginResult toLoginResult() {
    return LoginResult.valueOf(data.toString());
  }

  public List<String> toStringList() {
    LinkedList<String> fields = new LinkedList<>();
    RecordParser
        .newDelimited(FIELD_DELIMITER, field -> fields.add(field.toString()))
        .handle(data);
    return fields;
  }

}
