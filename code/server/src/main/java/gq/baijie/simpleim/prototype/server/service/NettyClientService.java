package gq.baijie.simpleim.prototype.server.service;

import com.google.protobuf.Any;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;

import gq.baijie.simpleim.prototype.server.io.network.netty.FrameToMessageFrameInboundHandler;
import gq.baijie.simpleim.prototype.server.io.network.netty.MessageFrameInboundHandler2;
import gq.baijie.simpleim.prototype.server.io.network.netty.MessageFrameToFrameOutboundHandler;
import gq.baijie.simpleim.prototype.server.proto.message.Message;
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

public class NettyClientService {

  @Nullable
  private MessageFrameInboundHandler2 businessHandler;

  @Inject
  public NettyClientService() {
  }

  public void start(String host, int port) {
    EventLoopGroup workerGroup = new NioEventLoopGroup();

    try {

      businessHandler = new MessageFrameInboundHandler2();

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

  public void stop() {
    if (businessHandler != null) {
      businessHandler.close();
    }
  }

  private static void sendEchoRequest(@Nonnull MessageFrameInboundHandler2 businessHandler) {
    final Message.Request request = Message.Request.newBuilder().setFunction("echo").build();
    businessHandler.getTransactionManager().newTransaction().send(request, frame -> {
      System.out.println("received response:");
      System.out.println(frame);
    });
  }

  public void sendEchoRequest() {
    if (businessHandler != null) {
      sendEchoRequest(businessHandler);
    }
  }

  private static void sendShutdownServerRequest(
      @Nonnull MessageFrameInboundHandler2 businessHandler) {
    final Message.Request request = Message.Request.newBuilder().setFunction("shutdown").build();
    businessHandler.getTransactionManager().newTransaction().send(request, frame -> {
      System.out.println("received response:");
      System.out.println(frame);
    });
  }

  public void sendShutdownServerRequest() {
    if (businessHandler != null) {
      sendShutdownServerRequest(businessHandler);
    }
  }

  public void sendCreateAccountRequest(@Nonnull String accountId, @Nonnull String password) {
    if (businessHandler == null) return;
    final Message.Request request = Message.Request.newBuilder()
        .setFunction("create account")
        .setMessage(Any.pack(Message.CreateAccountRequestMessage.newBuilder()
                                 .setAccountId(accountId)
                                 .setPassword(password)
                                 .build()))
        .build();
    businessHandler.getTransactionManager().newTransaction().send(request, frame -> {
      System.out.println("received response:");
      System.out.println(frame);
    });
  }

}
