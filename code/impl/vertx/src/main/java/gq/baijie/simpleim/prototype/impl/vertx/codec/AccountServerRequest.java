package gq.baijie.simpleim.prototype.impl.vertx.codec;

import javax.annotation.Nonnull;

public class AccountServerRequest<T> {

  public static final byte TYPE_REGISTER_REQUEST = 1;
  public static final byte TYPE_LOGIN_REQUEST = 2;
  public static final byte TYPE_LOGOUT_REQUEST = 3;
  public static final byte TYPE_GET_ONLINE_USERS_REQUEST = 4;

  public byte type;

  public T data;

  public static AccountServerRequest<RegisterRequestParameters> registerRequest(
      @Nonnull String accountId, @Nonnull String password) {
    RegisterRequestParameters parameters = new RegisterRequestParameters();
    parameters.accountId = accountId;
    parameters.password = password;

    AccountServerRequest<RegisterRequestParameters> request = new AccountServerRequest<>();
    request.type = TYPE_REGISTER_REQUEST;
    request.data = parameters;
    return request;
  }

  public static AccountServerRequest<LoginRequestParameters> loginRequest(
      @Nonnull String accountId, @Nonnull String password) {
    LoginRequestParameters parameters = new LoginRequestParameters();
    parameters.accountId = accountId;
    parameters.password = password;

    AccountServerRequest<LoginRequestParameters> request = new AccountServerRequest<>();
    request.type = TYPE_LOGIN_REQUEST;
    request.data = parameters;
    return request;
  }

  public static AccountServerRequest<LogoutRequestParameters> logoutRequest(
      @Nonnull String accountId) {
    LogoutRequestParameters parameters = new LogoutRequestParameters();
    parameters.accountId = accountId;

    AccountServerRequest<LogoutRequestParameters> request = new AccountServerRequest<>();
    request.type = TYPE_LOGOUT_REQUEST;
    request.data = parameters;
    return request;
  }

  public static AccountServerRequest<Void> getOnlineUsersRequest() {
    AccountServerRequest<Void> request = new AccountServerRequest<>();
    request.type = TYPE_GET_ONLINE_USERS_REQUEST;
    request.data = null;
    return request;
  }

  public static class RegisterRequestParameters {

    @Nonnull
    public String accountId;
    @Nonnull
    public String password;
  }
  public static class LoginRequestParameters {

    @Nonnull
    public String accountId;
    @Nonnull
    public String password;
  }
  public static class LogoutRequestParameters {

    @Nonnull
    public String accountId;
  }

}
