package gq.baijie.simpleim.prototype.io.network.netty.client.service;

import javax.inject.Inject;

import gq.baijie.simpleim.prototype.business.api.Result;
import gq.baijie.simpleim.prototype.io.network.api.message.Message;
import gq.baijie.simpleim.prototype.io.network.api.service.ServerManageService;
import gq.baijie.simpleim.prototype.io.network.netty.client.inject.ClientScope;
import gq.baijie.simpleim.prototype.io.network.netty.common.handler.MessageFrameInboundHandler2;
import rx.Observable;

@ClientScope
public class ServerManageServiceImpl implements ServerManageService {

  @Inject
  MessageFrameInboundHandler2 businessHandler;

  @Inject
  public ServerManageServiceImpl() {}

  @Override
  public Observable<Result<Void, Void>> shutdown() {
    return Observable.create(subscriber -> {
      businessHandler.getTransactionManager().newTransaction().send(buildRequest(), frame -> {
        if (subscriber.isUnsubscribed()) {
          return;
        }

        if (frame.getResponse().getResultCase() == Message.Response.ResultCase.SUCCESS_MESSAGE) {
          final Result<Void, Void> result = Result.succeed(null);
          subscriber.onNext(result);
        } else { // response error
          final Result<Void, Void> result = Result.error(null);
          subscriber.onNext(result);
        }
      });
    });
  }

  private static Message.Request buildRequest() {
    return Message.Request.newBuilder().setFunction("shutdown").build();
  }

}
