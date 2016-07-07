package gq.baijie.simpleim.prototype.io.network.netty.client.service;

import com.google.protobuf.Any;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import gq.baijie.simpleim.prototype.business.api.AccountService;
import gq.baijie.simpleim.prototype.business.api.Result;
import gq.baijie.simpleim.prototype.io.network.api.message.Message;
import gq.baijie.simpleim.prototype.io.network.netty.common.handler.MessageFrameInboundHandler2;
import rx.Observable;

public class AccountServiceImpl implements AccountService {

  @Inject
  MessageFrameInboundHandler2 businessHandler;

  @Inject
  public AccountServiceImpl() {
  }

  @Override
  public Observable<Result<Void, CreateError>> create(
      @Nonnull String accountId, @Nonnull String password) {
    return Observable.create(subscriber -> {
      final Message.Request request = buildCreateAccountRequest(accountId, password);
      businessHandler.getTransactionManager().newTransaction().send(request, frame -> {
        if (subscriber.isUnsubscribed()) {
          return;
        }

        if (frame.getResponse().getResultCase() == Message.Response.ResultCase.SUCCESS_MESSAGE) {
          final Result<Void, CreateError> result = Result.succeed(null);
          subscriber.onNext(result);
        } else { // response error
          subscriber.onNext(convertCreateAccountError(frame));
        }
      });
    });
  }

  @Override
  public Observable<Result<Void, LoginError>> login(
      @Nonnull String accountId, @Nonnull String password) {
    //TODO
    throw new UnsupportedOperationException();
  }

  @Override
  public Observable<Result<Void, LogoutError>> logout() {
    //TODO
    throw new UnsupportedOperationException();
  }

  private static Message.Request buildCreateAccountRequest(
      @Nonnull String accountId, @Nonnull String password) {
    return Message.Request.newBuilder()
        .setFunction("create account")
        .setMessage(Any.pack(Message.CreateAccountRequestMessage.newBuilder()
                                 .setAccountId(accountId)
                                 .setPassword(password)
                                 .build()))
        .build();
  }

  private static Result<Void, CreateError> convertCreateAccountError(Message.Frame frame)
      /*throws InvalidProtocolBufferException*/ {
    // TODO be consistent with server
    /*final Message.CreateAccountSuccessMessage responseMessage =
        frame.getResponse().getSuccessMessage().unpack(Message.CreateAccountSuccessMessage.class);
    final Message.CreateAccountFailureMessage failureMessage =
        frame.getResponse().getFailureCause().unpack(Message.CreateAccountFailureMessage.class);*/

    return Result.error(CreateError.DUPLICATED_ACCOUNT_ID);
  }

}
