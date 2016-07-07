package gq.baijie.simpleim.prototype.io.network.netty.common.handler;

import gq.baijie.simpleim.prototype.io.network.api.message.Message;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class FrameToMessageFrameInboundHandler extends ChannelInboundHandlerAdapter {

  @Override
  public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
    ByteBuf buffer = (ByteBuf) msg;
    final Message.Frame frame = Message.Frame.parseFrom(new ByteBufInputStream(buffer));
    buffer.release();
    ctx.fireChannelRead(frame);
  }

}
