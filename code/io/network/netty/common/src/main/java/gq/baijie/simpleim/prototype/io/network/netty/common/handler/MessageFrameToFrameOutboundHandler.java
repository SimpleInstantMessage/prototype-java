package gq.baijie.simpleim.prototype.io.network.netty.common.handler;

import gq.baijie.simpleim.prototype.io.network.api.message.Message;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;

public class MessageFrameToFrameOutboundHandler extends ChannelOutboundHandlerAdapter {

  @Override
  public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise)
      throws Exception {
    //super.write(ctx, msg, promise);
    Message.Frame message = (Message.Frame) msg;
    ByteBuf output = ctx.alloc().buffer(message.getSerializedSize());
    message.writeTo(new ByteBufOutputStream(output));
    ctx.write(output, promise);
  }

}
