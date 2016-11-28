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
    String accountId;
    @Nonnull
    String password;
  }


  interface LoginRequest extends Request<LoginRequestParameters, LoginResult> {

  }
  class LoginRequestParameters {

    @Nonnull
    String accountId;
    @Nonnull
    String password;
  }


  interface LogoutRequest extends Request<LogoutRequestParameters, Void> {

  }
  class LogoutRequestParameters {

    @Nonnull
    String accountId;
  }

  interface GetOnlineUsersRequest extends Request<Void, List<String>> {

  }

}
