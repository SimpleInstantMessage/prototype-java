package gq.baijie.simpleim.prototype.server.service;

import java.util.List;

import javax.annotation.Nonnull;

import gq.baijie.simpleim.prototype.business.api.AccountService.LoginResult;
import gq.baijie.simpleim.prototype.business.api.AccountService.RegisterResult;
import rx.Observable;

public interface AccountServerHandle {

  Observable<Request> requests();

  interface Request<P, R> {

    P parameters();

    void response(R result);
  }

  interface RegisterRequest extends Request<RegisterRequestParameters, RegisterResult> {

  }

  class RegisterRequestParameters {

    @Nonnull
    public String accountId;
    @Nonnull
    public String password;
  }


  interface LoginRequest extends Request<LoginRequestParameters, LoginResult> {

  }
  class LoginRequestParameters {

    @Nonnull
    public String accountId;
    @Nonnull
    public String password;
  }


  interface LogoutRequest extends Request<LogoutRequestParameters, Void> {

  }
  class LogoutRequestParameters {

    @Nonnull
    public String accountId;
  }

  interface GetOnlineUsersRequest extends Request<Void, List<String>> {

  }

}
