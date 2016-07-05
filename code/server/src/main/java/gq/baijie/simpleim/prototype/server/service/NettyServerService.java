package gq.baijie.simpleim.prototype.server.service;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Singleton;

import gq.baijie.simpleim.prototype.server.Main;
import gq.baijie.simpleim.prototype.server.io.network.netty.FrameToMessageFrameInboundHandler;
import gq.baijie.simpleim.prototype.server.io.network.netty.MessageFrameInboundHandler2;
import gq.baijie.simpleim.prototype.server.io.network.netty.MessageFrameToFrameOutboundHandler;
import gq.baijie.simpleim.prototype.server.io.network.netty.business.ServerRequestHandler;
import gq.baijie.simpleim.prototype.server.proto.message.Message;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import io.netty.handler.logging.LoggingHandler;

@Singleton
public class NettyServerService {

  @Nullable
  private Channel serverListeningChannel;

  @Inject
  public NettyServerService() {
  }

  public void start(int port) {
    ServerRequestHandler initRequestHandler = new ServerRequestHandler();
    initRequestHandler.getHandlers().put("echo", (transaction, frame) -> {
      transaction.send(frame.toBuilder()
                           .setTransactionState(Message.TransactionState.LAST)
                           .setResponse(Message.Response.newBuilder()
                                            .setSuccessMessage(frame.getRequest().getMessage())
                                            .build()));
    });
    initRequestHandler.getHandlers().put("shutdown", (transaction, frame) ->
        //TODO response
        Main.INSTANCE.serviceComponent.getSystemManagerService().shutdown()
    );

    EventLoopGroup bossGroup = new NioEventLoopGroup(); // (1)
    EventLoopGroup workerGroup = new NioEventLoopGroup();
    try {
      ServerBootstrap b = new ServerBootstrap(); // (2)
      b.group(bossGroup, workerGroup)
          .channel(NioServerSocketChannel.class) // (3)
          .childHandler(new ChannelInitializer<SocketChannel>() { // (4)
            @Override
            public void initChannel(SocketChannel ch) throws Exception {
              //ch.pipeline().addLast(new DiscardServerHandler());
//              ch.pipeline().addLast(new LoggingHandler('server'));
              ch.pipeline()
                  .addLast(new LoggingHandler("server"))
                  // outbound
                  .addLast(new ProtobufVarint32LengthFieldPrepender())
                  .addLast(new MessageFrameToFrameOutboundHandler())
                  // inbound
                  .addLast(new ProtobufVarint32FrameDecoder())
                  .addLast(new FrameToMessageFrameInboundHandler())
                  // business
                  .addLast(new MessageFrameInboundHandler2(initRequestHandler));
            }
          })
          .option(ChannelOption.SO_BACKLOG, 128)          // (5)
          .childOption(ChannelOption.SO_KEEPALIVE, true); // (6)

      // Bind and start to accept incoming connections.
      ChannelFuture f = b.bind(port).sync(); // (7)

      // Wait until the server socket is closed.
      // In this example, this does not happen, but you can do that to gracefully
      // shut down your server.
//      f.channel().closeFuture().sync();
      serverListeningChannel = f.channel();
      serverListeningChannel.closeFuture().addListener(listener -> {
        workerGroup.shutdownGracefully();
        bossGroup.shutdownGracefully();
      });
    } catch (InterruptedException e) {
      e.printStackTrace();//TODO like stop()?
    } finally {
//      workerGroup.shutdownGracefully();
//      bossGroup.shutdownGracefully();
    }
  }

  public void stop() {
    if (serverListeningChannel != null) {
      serverListeningChannel.close().syncUninterruptibly();
    }
  }

}
