package gq.baijie.simpleim.prototype.io.network.netty.common.business;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

import gq.baijie.simpleim.prototype.io.network.api.message.Message;

public class ServerRequestHandler
    implements BiConsumer<TransactionManager.Transaction, Message.Frame> {

  private final Map<String, BiConsumer<TransactionManager.Transaction, Message.Frame>> handlers =
      new HashMap<>();

  public Map<String, BiConsumer<TransactionManager.Transaction, Message.Frame>> getHandlers() {
    return handlers;
  }

  @Override
  public void accept(TransactionManager.Transaction transaction, Message.Frame frame) {
    final BiConsumer<TransactionManager.Transaction, Message.Frame> handler =
        handlers.get(frame.getRequest().getFunction());
    if (handler != null) {
      handler.accept(transaction, frame);
    } else {
      //TODO 404 error?
    }
  }


}
