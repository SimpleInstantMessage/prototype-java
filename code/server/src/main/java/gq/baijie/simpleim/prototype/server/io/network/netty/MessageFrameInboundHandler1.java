package gq.baijie.simpleim.prototype.server.io.network.netty;

import gq.baijie.simpleim.prototype.server.proto.message.Message;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class MessageFrameInboundHandler1 extends ChannelInboundHandlerAdapter {

  /*@Override
  void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
    def message = msg as MessageFrameOuterClass.MessageFrame
    println "at eventLoop: ${ctx.channel().eventLoop()}"
    println "at executor: ${ctx.executor()}"
    println "[${Thread.currentThread()}]client read:"
    println message

    [Request.SearchRequest, AccountMessage.CreateAccountResponse]
        .find { message.message.is(it) }
        .with {
          println message.message.unpack(it)
        }

    new Thread(){
      @Override
      void run() {
        sleep(2000)
        ctx.executor().submit{close(ctx)}
      }
    }.start();

  }

  private static void close(ChannelHandlerContext ctx) {
    ctx.close()
    ctx.channel().parent().close()
  }*/

  @Override
  public void channelActive(ChannelHandlerContext ctx) throws Exception {
    System.out.println("[client]channelActive");
    ctx.writeAndFlush(Message.Frame.newBuilder()
                          .setTransactionId(1)
                          .setTransactionState(Message.TransactionState.FIRST)
                          .setRequest(Message.Request.newBuilder().setFunction("echo").build())
                          .build());
    super.channelActive(ctx);
  }

  @Override
  public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
    System.out.println(msg);
  }

}
