package gq.baijie.simpleim.prototype.server;

import gq.baijie.simpleim.prototype.server.inject.DaggerServiceComponent;
import gq.baijie.simpleim.prototype.server.inject.ServiceComponent;
import gq.baijie.simpleim.prototype.server.io.network.netty.FrameToMessageFrameInboundHandler;
import gq.baijie.simpleim.prototype.server.io.network.netty.MessageFrameInboundHandler2;
import gq.baijie.simpleim.prototype.server.io.network.netty.MessageFrameToFrameOutboundHandler;
import gq.baijie.simpleim.prototype.server.proto.message.Message;
import gq.baijie.simpleim.prototype.server.service.NettyServerService;
import gq.baijie.simpleim.prototype.server.service.SystemManagerService;
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

public class Main {

  public static final Main INSTANCE = new Main();

  public ServiceComponent serviceComponent = DaggerServiceComponent.create();

  public static void main(String[] args) {

    System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "debug");
    System.setProperty("org.slf4j.simpleLogger.logFile", "log.log");
//    InternalLoggerFactory.setDefaultFactory(Slf4JLoggerFactory.INSTANCE);

    final ServiceComponent serviceComponent = INSTANCE.serviceComponent;
    final SystemManagerService systemManagerService = serviceComponent.getSystemManagerService();
    final NettyServerService nettyServerService = serviceComponent.getNettyServerService();
    systemManagerService.getStartTaskList().add(() -> nettyServerService.start(56789));
    systemManagerService.getStopTasksList().add(nettyServerService::stop);

    systemManagerService.start();

    tryNettyClient("localhost", 56789);
    tryNettyClient("localhost", 56789);
    tryNettyClient("localhost", 56789);

  }

  private static void tryNettyClient(String host, int port) {
    EventLoopGroup workerGroup = new NioEventLoopGroup();

    try {

      final MessageFrameInboundHandler2 businessHandler = new MessageFrameInboundHandler2();

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

      sendEchoRequest(businessHandler);

    } catch (InterruptedException e) {
      e.printStackTrace();
    } finally {
//      workerGroup.shutdownGracefully();
    }
  }

  private static void sendEchoRequest(MessageFrameInboundHandler2 businessHandler) {
    final Message.Frame.Builder echoRequest = Message.Frame.newBuilder()
        .setTransactionState(Message.TransactionState.FIRST)
        .setRequest(Message.Request.newBuilder().setFunction("echo").build());
    businessHandler.getTransactionManager().newTransaction().send(echoRequest, frame -> {
      System.out.println("received response:");
      System.out.println(frame);
      businessHandler.close();
    });
  }

  private static void println(Object o) {
    System.out.println(o);
  }

}
