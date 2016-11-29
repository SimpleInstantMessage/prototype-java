package gq.baijie.simpleim.prototype.server.impl.vertx;

import javax.inject.Inject;
import javax.inject.Singleton;

import gq.baijie.simpleim.prototype.business.api.Message;
import gq.baijie.simpleim.prototype.business.api.MessageSwitchService;
import gq.baijie.simpleim.prototype.server.impl.vertx.codec.Record;
import rx.Observable;
import rx.subjects.PublishSubject;

@Singleton
public class RemoteMessageSwitchService implements MessageSwitchService {

  private final RemoteChannelService channelService;

  private final PublishSubject<Message> messages = PublishSubject.create();

  private final Session session = new VertxSession();

  @Inject
  public RemoteMessageSwitchService(RemoteChannelService channelService) {
    this.channelService = channelService;
    init();
  }

  private void init() {
    channelService.records()
        .map(record -> record.data)
        .ofType(Message.class)
        .subscribe(messages);
  }

  @Override
  public MessageSwitchService.Session connect() {
    return session;
  }

  private class VertxSession implements Session {

    @Override
    public void sendMessage(Message message) {
      channelService.writeRecord(Record.of(message));
    }

    @Override
    public Observable<Message> receiveMessages() {
      return messages;
    }

    @Override
    public void close() {

    }
  }

}
