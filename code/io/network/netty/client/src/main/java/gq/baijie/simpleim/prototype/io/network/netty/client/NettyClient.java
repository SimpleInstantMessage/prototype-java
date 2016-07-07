package gq.baijie.simpleim.prototype.io.network.netty.client;

import javax.inject.Inject;
import javax.inject.Named;

import gq.baijie.simpleim.prototype.io.network.api.Client;
import gq.baijie.simpleim.prototype.io.network.netty.client.inject.ClientScope;
import gq.baijie.simpleim.prototype.io.network.netty.common.handler.FrameToMessageFrameInboundHandler;
import gq.baijie.simpleim.prototype.io.network.netty.common.handler.MessageFrameInboundHandler2;
import gq.baijie.simpleim.prototype.io.network.netty.common.handler.MessageFrameToFrameOutboundHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import io.netty.handler.logging.LoggingHandler;

@ClientScope
public class NettyClient implements Client {

  @Inject
  @Named("services")
  Object[] services;

  @Inject
  MessageFrameInboundHandler2 businessHandler;

  @Inject
  public NettyClient() {
  }

  @Override
  public void connect(String host, int port) { //TODO avoid called twice
    EventLoopGroup workerGroup = new NioEventLoopGroup();

    try {
      Bootstrap b = new Bootstrap(); // (1)
      b.group(workerGroup); // (2)
      b.channel(NioSocketChannel.class); // (3)
      b.option(ChannelOption.SO_KEEPALIVE, true); // (4)
      b.handler(new ChannelInitializer<SocketChannel>() {
        @Override
        public void initChannel(SocketChannel ch) throws Exception {
//          ch.pipeline().addLast(new TimeClientHandler());
          ch.pipeline()
              .addLast(new LoggingHandler("client"))
              // outbound
              .addLast(new ProtobufVarint32LengthFieldPrepender())
              .addLast(new MessageFrameToFrameOutboundHandler())
              // inbound
              .addLast(new ProtobufVarint32FrameDecoder())
              .addLast(new FrameToMessageFrameInboundHandler())
              // business
              .addLast(businessHandler);
//            addLast(new ClientHandler())
//            .addLast(new CreateAccountHandler());
        }
      });

      // Start the client.
      ChannelFuture f = b.connect(host, port).sync(); // (5)

      // Wait until the connection is closed.
//      f.channel().closeFuture().sync();
      f.channel().closeFuture().addListener(listener -> {
        workerGroup.shutdownGracefully();
      });

    } catch (InterruptedException e) {
      e.printStackTrace();
    } finally {
//      workerGroup.shutdownGracefully();
    }
  }

  @Override
  public void disconnect() {
    if (businessHandler != null) {
      businessHandler.close();
    }
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T> T getService(Class<T> clazz) {
    for (Object service : services) {
      if (clazz.isInstance(service)) {
        return (T) service;
      }
    }
    return null;
  }

}
