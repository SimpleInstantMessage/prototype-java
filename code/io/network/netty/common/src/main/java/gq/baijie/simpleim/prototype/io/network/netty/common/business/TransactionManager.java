package gq.baijie.simpleim.prototype.io.network.netty.common.business;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import gq.baijie.simpleim.prototype.io.network.api.message.Message;
import gq.baijie.simpleim.prototype.io.network.netty.common.handler.MessageFrameInboundHandler2;

public class TransactionManager {

  final MessageFrameInboundHandler2 handler;

  Map<Integer, Transaction> transactions = new HashMap<>();

  @Nullable
  BiConsumer<Transaction, Message.Frame> initRequestHandler;

  public TransactionManager(MessageFrameInboundHandler2 handler) {
    this.handler = handler;
  }

  public void setInitRequestHandler(
      @Nullable BiConsumer<Transaction, Message.Frame> initRequestHandler) {
    this.initRequestHandler = initRequestHandler;
  }

  public void onReceive(Message.Frame frame) {
    final Transaction transaction = getTransactionFor(frame);
    if (transaction != null) {
      transaction.onReceive(frame);
    }//TODO else
  }

  public void send(Message.Frame frame) {
    getTransactionFor(frame);//check TransactionState
    handler.send(frame);
  }

  private Transaction getTransactionFor(Message.Frame frame) {
    switch (frame.getTransactionState()) {
      case FIRST:
        if (transactions.containsKey(frame.getTransactionId())) {
          //TODO client server create same transaction id in the same time
          //TODO error
          return null;
        } else {
          final Transaction transaction =
              new Transaction(frame.getTransactionId(), TransactionState.HAVE_SENT);
          transaction.setRequestHandler(initRequestHandler);
          transactions.put(frame.getTransactionId(), transaction);
          return transaction;
        }
      case KEEP:
        if (transactions.containsKey(frame.getTransactionId())) {
          return transactions.get(frame.getTransactionId());
        } else {
          //TODO error
          return null;
        }
      case LAST:
        if (transactions.containsKey(frame.getTransactionId())) {
          return transactions.remove(frame.getTransactionId());
        } else {
          //TODO error
          return null;
        }
      case UNSET: case UNRECOGNIZED: default:
        //TODO error
        throw new UnsupportedOperationException("unsupported TransactionState");
    }
  }

  //TODO IMPORTANT thread
  public Transaction newTransaction() {
    int id;
    do {
      id = (int) (Math.random() * Integer.MAX_VALUE);
    } while (transactions.containsKey(id));
    Transaction transaction = new Transaction(id, TransactionState.HAVE_NOT_SENT);
    transactions.put(id, transaction);
    return transaction;
  }


  public class Transaction {

    public final int id;

    @Nonnull
    private TransactionState transactionState;

    BiConsumer<Transaction, Message.Frame> requestHandler;

    Queue<Request> pendingRequests = new LinkedList<>();

    private Transaction(int id, @Nonnull TransactionState state) {
      this.id = id;
      this.transactionState = state;
    }

    public void setRequestHandler(@Nullable BiConsumer<Transaction, Message.Frame> requestHandler) {
      this.requestHandler = requestHandler;
    }

    public void onReceive(Message.Frame frame) {
      switch (frame.getMessageCase()) {
        case REQUEST:
          if (requestHandler != null) {
            requestHandler.accept(this, frame);
          }
          break;
        case RESPONSE:
          Request request = pendingRequests.poll();
          request.onReceive(frame);
          break;
        case MESSAGE_NOT_SET:
        default:
          //TODO send error
          throw new UnsupportedOperationException("unknown message type in frame");
      }
    }

    private void send0(Message.Frame frame) {
      TransactionManager.this.send(frame);
      transactionState = TransactionState.HAVE_SENT;
    }

    public void send(Message.Request request, @Nullable Consumer<Message.Frame> responseHandler) {
      Message.Frame frame = buildMessageFrame(request, false);
      pendingRequests.offer(new Request(responseHandler));
      send0(frame);
    }

    public void send(Message.Response response) {
      Message.Frame frame = buildMessageFrame(response, false);
      send0(frame);
    }

    public void end(Message.Response response) {
      Message.Frame frame = buildMessageFrame(response, true);
      send0(frame);
    }

    private Message.Frame.Builder newMessageFrameBuilder(boolean isEnd) {
      final Message.Frame.Builder frameBuilder = Message.Frame.newBuilder();
      frameBuilder.setTransactionId(id);
      if (transactionState == TransactionState.HAVE_NOT_SENT) {
        frameBuilder.setTransactionState(Message.TransactionState.FIRST);//TODO see message.proto
      } else {
        if (isEnd) {
          frameBuilder.setTransactionState(Message.TransactionState.LAST);//TODO see message.proto
        } else {
          frameBuilder.setTransactionState(Message.TransactionState.KEEP);
        }
      }
      return frameBuilder;
    }

    private Message.Frame buildMessageFrame(Message.Request request, boolean isEnd) {
      return newMessageFrameBuilder(isEnd).setRequest(request).build();
    }
    private Message.Frame buildMessageFrame(Message.Response response, boolean isEnd) {
      return newMessageFrameBuilder(isEnd).setResponse(response).build();
    }



    class Request {

      private final Consumer<Message.Frame> responseHandler;

      Request(Consumer<Message.Frame> responseHandler) {
        this.responseHandler = responseHandler;
      }

      public void onReceive(Message.Frame frame) {
        if (responseHandler != null) {
          responseHandler.accept(frame);
        }
      }

      public void send(Message.Frame frame) {
//        Transaction.this.send();
      }
    }

  }

  private enum TransactionState {
    HAVE_NOT_SENT,
    HAVE_SENT
  }

}
