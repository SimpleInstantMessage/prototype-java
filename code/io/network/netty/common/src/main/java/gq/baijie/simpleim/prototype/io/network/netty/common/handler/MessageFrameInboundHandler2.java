package gq.baijie.simpleim.prototype.io.network.netty.common.handler;

import java.util.function.BiConsumer;

import javax.annotation.Nullable;

import gq.baijie.simpleim.prototype.io.network.api.message.Message;
import gq.baijie.simpleim.prototype.io.network.netty.common.business.TransactionManager;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;


public class MessageFrameInboundHandler2 extends ChannelInboundHandlerAdapter {

  private final TransactionManager transactionManager;

  private ChannelHandlerContext ctx;

  public MessageFrameInboundHandler2() {
    this(null);
  }

  public MessageFrameInboundHandler2(
      @Nullable BiConsumer<TransactionManager.Transaction, Message.Frame> initRequestHandler) {
    transactionManager = new TransactionManager(this);
    transactionManager.setInitRequestHandler(initRequestHandler);
  }

  public TransactionManager getTransactionManager() {
    return transactionManager;
  }

  @Override
  public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
    this.ctx = ctx;
  }

  @Override
  public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
    this.ctx = null;
  }

  @Override
  public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
    Message.Frame frame = (Message.Frame) msg;
    transactionManager.onReceive(frame);//TODO thread
  }

  private static void close(ChannelHandlerContext ctx) {
    ctx.close();
  }

  //TODO return Future object
  public void close() {
    if (ctx != null) { //TODO ctx == null
      close(ctx);
    }
  }

  public void send(Message.Frame frame) {
    if (ctx == null) {
      return;
      //TODO
    }
    ctx.executor().submit(() -> ctx.writeAndFlush(frame));
  }

}
