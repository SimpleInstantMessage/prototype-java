package gq.baijie.simpleim.prototype.io.network.netty.client.service;

import com.google.protobuf.Any;
import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;

import javax.inject.Inject;

import gq.baijie.simpleim.prototype.business.api.Result;
import gq.baijie.simpleim.prototype.io.network.api.message.Message;
import gq.baijie.simpleim.prototype.io.network.api.service.EchoService;
import gq.baijie.simpleim.prototype.io.network.netty.client.inject.ClientScope;
import gq.baijie.simpleim.prototype.io.network.netty.common.handler.MessageFrameInboundHandler2;
import rx.Observable;

@ClientScope
public class EchoServiceImpl implements EchoService {

  @Inject
  MessageFrameInboundHandler2 businessHandler;

  @Inject
  public EchoServiceImpl() {
  }

  @Override
  public Observable<Result<byte[], Void>> echo(byte[] bytes) {
    return Observable.create(subscriber -> {
      final Message.Request request = buildRequest(bytes);
      businessHandler.getTransactionManager().newTransaction().send(request, frame -> {
        if (subscriber.isUnsubscribed()) {
          return;
        }

        if (frame.getResponse().getResultCase() == Message.Response.ResultCase.SUCCESS_MESSAGE) {
          try {
            subscriber.onNext(convertResponse(frame));
          } catch (InvalidProtocolBufferException e) {
            subscriber.onError(e);//TODO
          }
        } else { // response error
          final Result<byte[], Void> result = Result.error(null);
          subscriber.onNext(result);
        }
      });
    });
  }

  private static Message.Request buildRequest(byte[] bytes) {
    final Message.EchoMessage message =
        Message.EchoMessage.newBuilder().setValue(ByteString.copyFrom(bytes)).build();
    return Message.Request.newBuilder().setFunction("echo").setMessage(Any.pack(message)).build();
  }

  private static Result<byte[], Void> convertResponse(Message.Frame frame)
      throws InvalidProtocolBufferException {
    final Message.EchoMessage responseMessage =
        frame.getResponse().getSuccessMessage().unpack(Message.EchoMessage.class);
    return Result.succeed(responseMessage.getValue().toByteArray());
  }

}
