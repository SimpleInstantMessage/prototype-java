package gq.baijie.simpleim.prototype.server.inject;

import dagger.Module;
import dagger.Provides;
import gq.baijie.simpleim.prototype.server.io.network.netty.business.ServerRequestHandler;
import gq.baijie.simpleim.prototype.server.proto.message.Message;
import gq.baijie.simpleim.prototype.server.service.SystemManagerService;

@Module
public class NetworkIoModule {

  @Provides
  ServerRequestHandler provideServerRequestHandler(SystemManagerService systemManagerService) {
    final ServerRequestHandler initRequestHandler = new ServerRequestHandler();
    initRequestHandler.getHandlers().put("echo", (transaction, frame) -> {
      transaction.send(frame.toBuilder()
                           .setTransactionState(Message.TransactionState.LAST)
                           .setResponse(Message.Response.newBuilder()
                                            .setSuccessMessage(frame.getRequest().getMessage())
                                            .build()));
    });
    initRequestHandler.getHandlers().put("shutdown", (transaction, frame) ->
        //TODO response
        systemManagerService.shutdown()
    );
    return initRequestHandler;
  }

}
