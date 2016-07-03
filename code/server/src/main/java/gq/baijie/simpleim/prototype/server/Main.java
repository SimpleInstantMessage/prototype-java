package gq.baijie.simpleim.prototype.server;

import java.util.function.BiConsumer;

import gq.baijie.simpleim.prototype.server.inject.DaggerServiceComponent;
import gq.baijie.simpleim.prototype.server.inject.ServiceComponent;
import gq.baijie.simpleim.prototype.server.io.network.netty.FrameToMessageFrameInboundHandler;
import gq.baijie.simpleim.prototype.server.io.network.netty.MessageFrameInboundHandler2;
import gq.baijie.simpleim.prototype.server.io.network.netty.MessageFrameInboundHandler3;
import gq.baijie.simpleim.prototype.server.io.network.netty.MessageFrameToFrameOutboundHandler;
import gq.baijie.simpleim.prototype.server.io.network.netty.business.TransactionManager;
import gq.baijie.simpleim.prototype.server.proto.message.Message;
import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import io.netty.handler.logging.LoggingHandler;

public class Main {

  public static final Main INSTANCE = new Main();

  public ServiceComponent serviceComponent = DaggerServiceComponent.create();

  public static void main(String[] args) {

    System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "debug");
    System.setProperty("org.slf4j.simpleLogger.logFile", "log.log");
//    InternalLoggerFactory.setDefaultFactory(Slf4JLoggerFactory.INSTANCE);

    tryNettyServer(56789);
    tryNettyClient("localhost", 56789);
    tryNettyClient("localhost", 56789);
    tryNettyClient("localhost", 56789);

  }

  private static void tryNettyServer(int port) {
    BiConsumer<TransactionManager.Transaction, Message.Frame>
        initRequestHandler = (transactions, frame) -> {
      final Message.Request frameRequest = frame.getRequest();
      switch (frameRequest.getFunction()) {//TODO
        case "echo":
          transactions.send(frame.toBuilder()
                                .setTransactionState(Message.TransactionState.LAST)
                                .setResponse(Message.Response.newBuilder()
                                                 .setSuccessMessage(frameRequest.getMessage())
                                                 .build()));
          break;
        default:
          //TODO error
          break;
      }
    };

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
      f.channel().closeFuture().addListener(listener -> {
        workerGroup.shutdownGracefully();
        bossGroup.shutdownGracefully();
      });
    } catch (InterruptedException e) {
      e.printStackTrace();
    } finally {
//      workerGroup.shutdownGracefully();
//      bossGroup.shutdownGracefully();
    }
  }

  private static void tryNettyClient(String host, int port) {
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
              .addLast(new MessageFrameInboundHandler3());
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

  private static void println(Object o) {
    System.out.println(o);
  }

}
