package gq.baijie.simpleim.prototype.server.impl.vertx;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import gq.baijie.simpleim.prototype.business.api.Message;
import gq.baijie.simpleim.prototype.business.api.MessageSwitchService;
import gq.baijie.simpleim.prototype.server.service.Server;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetServer;
import io.vertx.core.net.NetSocket;
import io.vertx.core.parsetools.RecordParser;
import rx.Observable;
import rx.subjects.PublishSubject;

public class VertxServer implements Server {

  private final Logger logger = LoggerFactory.getLogger(VertxServer.class);

  private final PublishSubject<NewConnectEvent> connects = PublishSubject.create();

  private final Vertx vertx = Vertx.vertx();
  private NetServer server;

  @Inject
  public VertxServer() {
  }

  @Override
  public void start() {
    if (server != null) {
      return;
    }
//    NetServerOptions options = new NetServerOptions().setPort(4321);
    server = vertx.createNetServer(/*options*/);

    server.connectHandler(socket -> {
      final MessageSwitchService.Session session = new VertxSession(socket);
      connects.onNext(() -> Observable.just(session));
    });

    server.listen(4321, res -> {
      if (res.succeeded()) {
        logger.info("Server is now listening!");
      } else {
        logger.info("Failed to bind!");
      }
    });
  }

  @Override
  public void stop() {
    if (server == null) {
      return;
    }
    server.close();
    server = null;
  }

  @Override
  public Observable<NewConnectEvent> connects() {
    return connects;
  }

  private static class VertxSession implements MessageSwitchService.Session {
    // UTF-8 doesn't contain FIELD_DELIMITER and RECORD_DELIMITER
    private static final Buffer FIELD_DELIMITER = Buffer.buffer(1).appendByte((byte) 0b1100_0000);
    private static final Buffer RECORD_DELIMITER = Buffer.buffer(1).appendByte((byte) 0b1100_0001);

    private final PublishSubject<Message> receiveMessages = PublishSubject.create();

    private final NetSocket socket;

    private VertxSession(NetSocket socket) {
      this.socket = socket;
      socket.handler(RecordParser.newDelimited(RECORD_DELIMITER, buffer -> {
        LinkedList<String> fields = new LinkedList<>();
        RecordParser.newDelimited(FIELD_DELIMITER, field -> fields.add(field.toString())).handle(buffer);

        final List<Message.Receiver> receivers = fields.stream()
            .limit(fields.size() - 2)
            .map(Message.Receiver::new)
            .collect(Collectors.toList());
        final String senderId = fields.get(fields.size()-2);
        final String message = fields.get(fields.size()-1);

        receiveMessages.onNext(new Message(senderId, receivers, message));
      }));
    }

    @Override
    public void sendMessage(Message message) {
      // Write strings in UTF-8 encoding
      message.getReceivers().forEach(r -> socket.write(r.getReceiverId()).write(FIELD_DELIMITER));
      socket.write(message.getSenderId()).write(FIELD_DELIMITER);
      socket.write(message.getMessage()).write(RECORD_DELIMITER);
    }

    @Override
    public Observable<Message> receiveMessages() {
      return receiveMessages;
    }

    @Override
    public void close() {
      socket.close();
    }
  }

}
