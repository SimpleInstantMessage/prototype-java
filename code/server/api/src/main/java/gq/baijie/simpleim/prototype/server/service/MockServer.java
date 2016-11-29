package gq.baijie.simpleim.prototype.server.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import gq.baijie.simpleim.prototype.business.api.AccountService.LoginResult;
import gq.baijie.simpleim.prototype.business.api.Message;
import gq.baijie.simpleim.prototype.business.api.MessageSwitchService;
import rx.Observable;
import rx.subjects.PublishSubject;

public class MockServer implements Server {

  private final Logger logger = LoggerFactory.getLogger(MockServer.class);

  private final PublishSubject<NewConnectEvent> connects = PublishSubject.create();

  private final NewConnectEvent mockNewConnectEvent = () -> Observable.just(
      new MockSession(),
      new MockAccountServerHandle2()
  );

  @Inject
  public MockServer() {
  }

  @Override
  public void start() {
    fireNewConnectEvent();
  }

  @Override
  public void stop() {
  }

  @Override
  public Observable<NewConnectEvent> connects() {
    return connects;
  }

  public void fireNewConnectEvent() {
    connects.onNext(mockNewConnectEvent);
  }

  private class MockSession implements MessageSwitchService.Session {

    @Override
    public void sendMessage(Message message) {
      logger.info("sendMessage(message: {})", message);
    }

    @Override
    public Observable<Message> receiveMessages() {
      final List<Message.Receiver> mockReceivers =
          Collections.singletonList(new Message.Receiver("mockReceiverId"));
      return Observable.just(new Message("mockSenderId", mockReceivers, "mock message"));
    }

    @Override
    public void close() {
    }
  }

  private class MockAccountServerHandle2 implements AccountServerHandle2 {

    @Override
    public void setOnReceiveRequestListener(OnReceiveRequestListener listener) {
      final LoginResult result = listener.onReceiveLoginRequest("baijie", "baijie");
      logger.info("MockAccountServerHandle2 receive LoginResult: {}", result);
    }
  }

}
