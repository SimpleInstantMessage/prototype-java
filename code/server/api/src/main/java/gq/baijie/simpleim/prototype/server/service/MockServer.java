package gq.baijie.simpleim.prototype.server.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import gq.baijie.simpleim.prototype.business.api.AccountService.LoginResult;
import gq.baijie.simpleim.prototype.business.api.Message;
import rx.Observable;
import rx.subjects.PublishSubject;

public class MockServer implements Server {

  private final Logger logger = LoggerFactory.getLogger(MockServer.class);

  private final PublishSubject<Connect> connects = PublishSubject.create();

  private final Connect mockConnect = () -> Observable.just(
      new MockMessageSwitchServerHandle(),
      new MockAccountServerHandle()
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
  public Observable<Connect> connects() {
    return connects;
  }

  public void fireNewConnectEvent() {
    connects.onNext(mockConnect);
  }

  private class MockMessageSwitchServerHandle implements MessageSwitchServerHandle {

    @Override
    public void setOnReceiveRequestListener(OnReceiveRequestListener listener) {
      final List<Message.Receiver> mockReceivers =
          Collections.singletonList(new Message.Receiver("mockReceiverId"));
      listener.onReceiveMessage(new Message("mockSenderId", mockReceivers, "mock message"));
    }

    @Override
    public void sendMessage(Message message) {
      logger.info("sendMessage(message: {})", message);
    }
  }

  private class MockAccountServerHandle implements AccountServerHandle {

    @Override
    public void setOnReceiveRequestListener(OnReceiveRequestListener listener) {
      final LoginResult result = listener.onReceiveLoginRequest("baijie", "baijie");
      logger.info("MockAccountServerHandle receive LoginResult: {}", result);
    }
  }

}
