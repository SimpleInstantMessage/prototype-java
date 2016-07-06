package gq.baijie.simpleim.prototype.server.inject;

import com.google.protobuf.Any;
import com.google.protobuf.InvalidProtocolBufferException;

import dagger.Module;
import dagger.Provides;
import gq.baijie.simpleim.prototype.server.io.network.netty.business.ServerRequestHandler;
import gq.baijie.simpleim.prototype.server.proto.message.Message;
import gq.baijie.simpleim.prototype.server.service.AccountService;
import gq.baijie.simpleim.prototype.server.service.SystemManagerService;

@Module
public class NetworkIoModule {

  @Provides
  ServerRequestHandler provideServerRequestHandler(
      SystemManagerService systemManagerService,
      AccountService accountService) {
    final ServerRequestHandler initRequestHandler = new ServerRequestHandler();
    initRequestHandler.getHandlers().put("echo", (transaction, frame) -> {
      transaction.end(Message.Response.newBuilder()
                           .setSuccessMessage(frame.getRequest().getMessage())
                           .build());
    });
    initRequestHandler.getHandlers().put("shutdown", (transaction, frame) ->
        //TODO response
        systemManagerService.shutdown()
    );

    initRequestHandler.getHandlers().put("create account", (transaction, frame) -> {
      try {
        final Message.CreateAccountRequestMessage request =
            frame.getRequest().getMessage().unpack(Message.CreateAccountRequestMessage.class);
        final AccountService.RegisterResult result =
            accountService.register(request.getAccountId(), request.getPassword());
        if (result == AccountService.RegisterResult.SUCCESS) {
          final Message.CreateAccountSuccessMessage successMessage =
              Message.CreateAccountSuccessMessage.getDefaultInstance();
          transaction.end(Message.Response.newBuilder()
                               .setSuccessMessage(Any.pack(successMessage))
                               .build());
        } else {
          final Message.CreateAccountFailureMessage failureMessage =
              Message.CreateAccountFailureMessage.newBuilder()
                  .setErrorCode(result.ordinal()) //TODO
                  .setErrorMessage(result.toString()) //TODO
                  .build();
          transaction.end(Message.Response.newBuilder()
                               .setFailureCause(Any.pack(failureMessage))
                               .build());
        }
      } catch (InvalidProtocolBufferException e) {
        e.printStackTrace();//TODO
      }
    });

    return initRequestHandler;
  }

}
