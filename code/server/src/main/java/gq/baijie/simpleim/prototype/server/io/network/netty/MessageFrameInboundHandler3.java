package gq.baijie.simpleim.prototype.server.io.network.netty;

import gq.baijie.simpleim.prototype.server.proto.message.Message;
import io.netty.channel.ChannelHandlerContext;


public class MessageFrameInboundHandler3 extends MessageFrameInboundHandler2 /*implements Port*/ {

  @Override
  public void channelActive(ChannelHandlerContext ctx) throws Exception {
    final Message.Frame.Builder echoRequest = Message.Frame.newBuilder()
        .setTransactionState(Message.TransactionState.FIRST)
        .setRequest(Message.Request.newBuilder().setFunction("echo").build());
    transactionManager.newTransaction().send(echoRequest, frame -> {
      System.out.println("received response:");
      System.out.println(frame);
    });
    super.channelActive(ctx);
  }

}
