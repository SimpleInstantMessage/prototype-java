package gq.baijie.simpleim.prototype.business.api;

import javax.annotation.Nonnull;

public interface AccountService {

  Result<Void, CreateError> create(@Nonnull String accountId, @Nonnull String password);

  Result<Void, LoginError> login(@Nonnull String accountId, @Nonnull String password);

  Result<Void, LogoutError> logout();

  enum CreateError {
    DUPLICATED_ACCOUNT_ID
  }

  enum LoginError {
    NO_ACCOUNT,
    PASSWORD_ERROR
  }

  enum LogoutError {
    HAVE_NOT_LOGGED_IN
  }

}
