package gq.baijie.simpleim.prototype.business.api;

import java.util.List;

import javax.annotation.Nonnull;

public interface AccountService {

  RegisterResult register(@Nonnull String accountId, @Nonnull String password);

  LoginResult login(@Nonnull String accountId, @Nonnull String password);

  void logout(@Nonnull String accountId);

  /**
   * get list of account id who are online now
   */
  List<String> onlineUsers();

  enum RegisterResult {
    SUCCESS,
    DUPLICATED_ACCOUNT_ID
  }

  enum LoginResult {
    SUCCESS,
    NO_ACCOUNT,
    PASSWORD_ERROR
  }

}
