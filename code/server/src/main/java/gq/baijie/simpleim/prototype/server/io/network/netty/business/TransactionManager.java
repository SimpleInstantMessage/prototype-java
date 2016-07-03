package gq.baijie.simpleim.prototype.server.io.network.netty.business;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import javax.annotation.Nullable;

import gq.baijie.simpleim.prototype.server.io.network.netty.MessageFrameInboundHandler2;
import gq.baijie.simpleim.prototype.server.proto.message.Message;

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
          final Transaction transaction = new Transaction(frame.getTransactionId());
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
    Transaction transaction = new Transaction(id);
    transactions.put(id, transaction);
    return transaction;
  }


  public class Transaction {

    public final int id;

    BiConsumer<Transaction, Message.Frame> requestHandler;

    Queue<Request> pendingRequests = new LinkedList<>();

    public Transaction(int id) {
      this.id = id;
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

    public void send(Message.Frame.Builder frameBuilder) {
      send(frameBuilder, null);
    }

    public void send(Message.Frame.Builder frameBuilder, Consumer<Message.Frame> responseHandler) {
      Message.Frame frame = frameBuilder.setTransactionId(id).build();
      switch (frame.getMessageCase()) {
        case REQUEST:
          Request request = new Request(responseHandler);
          pendingRequests.offer(request);
          TransactionManager.this.send(frame);
          break;
        case RESPONSE:
          TransactionManager.this.send(frame);
          break;
        case MESSAGE_NOT_SET:
        default:
          //TODO send error
          throw new UnsupportedOperationException("unknown message type in frame");
      }
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

}
