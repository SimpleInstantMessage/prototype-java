package gq.baijie.simpleim.prototype.business.server.mock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

import javax.inject.Inject;

import gq.baijie.simpleim.prototype.business.common.AccountService.LoginResult;
import gq.baijie.simpleim.prototype.business.common.Message;
import gq.baijie.simpleim.prototype.business.server.Server;
import gq.baijie.simpleim.prototype.business.server.session.AccountSession;
import gq.baijie.simpleim.prototype.business.server.session.MessageSwitchSession;
import rx.Observable;
import rx.subjects.PublishSubject;

public class MockServer implements Server {

  private final Logger logger = LoggerFactory.getLogger(MockServer.class);

  private final PublishSubject<Connect> connects = PublishSubject.create();

  private final Connect mockConnect = new MockConnect(Arrays.asList(
      new MockAccountSession(),
      new MockMessageSwitchSession()
  ));

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

  private class MockConnect implements Connect {

    private final List<Session> sessions;

    private MockConnect(List<Session> sessions) {
      this.sessions = sessions;
    }

    @Override
    public void setOnCloseListener(Consumer<Connect> listener) {

    }

    @Override
    public Observable<Session> sessions() {
      return Observable.from(sessions);
    }
  }

  private class MockMessageSwitchSession implements MessageSwitchSession {

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

  private class MockAccountSession implements AccountSession {

    @Override
    public void setOnReceiveRequestListener(OnReceiveRequestListener listener) {
      final LoginResult result = listener.onReceiveLoginRequest("baijie", "baijie");
      logger.info("MockAccountSession receive LoginResult: {}", result);
    }
  }

}
