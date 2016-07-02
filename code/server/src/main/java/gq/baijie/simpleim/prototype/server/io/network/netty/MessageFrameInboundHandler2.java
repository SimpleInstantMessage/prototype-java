package gq.baijie.simpleim.prototype.server.io.network.netty;

import java.util.concurrent.atomic.AtomicLong;

import gq.baijie.simpleim.prototype.server.io.network.netty.business.TransactionManager;
import gq.baijie.simpleim.prototype.server.proto.message.Message;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;


public class MessageFrameInboundHandler2 extends ChannelInboundHandlerAdapter /*implements Port*/ {

  private static AtomicLong counter = new AtomicLong();

  private final long id = counter.incrementAndGet();

  private final String address = "client:$id";

  final TransactionManager transactionManager = new TransactionManager(this);

  private ChannelHandlerContext ctx;

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
//    def message = msg as MessageFrameOuterClass.MessageFrame;
//    Routers.defaultRouter.send(address, "service:${message.serviceId}", message.sessionId, message.message);

//    println message

    /*new Thread(){
      @Override
      void run() {
        sleep(2000);
        ctx.executor().submit{close(ctx)};;
      }
    }.start();*/

  }

  private static void close(ChannelHandlerContext ctx) {
    ctx.close();
    ctx.channel().parent().close();
  }

  public void send(Message.Frame frame) {
    if (ctx == null) {
      return;
      //TODO
    }
    ctx.executor().submit(()-> ctx.writeAndFlush(frame));
  }

  /*@Override
  void onReceive(Message businessMessage) {
    ctx?.executor()?.submit{
      def message = MessageFrameOuterClass.MessageFrame.newBuilder()
          .setServiceId(toServiceId(businessMessage.sender))
          .setSessionId(businessMessage.sessionId)
          .setMessage(businessMessage.message as Any)
          .build()
      ctx?.writeAndFlush(message)
    }
  }*/

  /*private static String toServiceId(String serviceAddress) {
    serviceAddress.replace('service:', '');
  }*/

}
