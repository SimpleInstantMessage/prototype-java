package gq.baijie.simpleim.prototype.server.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import gq.baijie.simpleim.prototype.business.api.AccountService;
import gq.baijie.simpleim.prototype.business.api.Message;
import gq.baijie.simpleim.prototype.business.api.MessageSwitchService;
import rx.Observable;
import rx.subjects.PublishSubject;

public class MockServer implements Server {

  private final Logger logger = LoggerFactory.getLogger(MockServer.class);

  private final PublishSubject<NewConnectEvent> connects = PublishSubject.create();

  private final NewConnectEvent mockNewConnectEvent = () -> Observable.just(
      new MockSession(),
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

  private class MockAccountServerHandle implements AccountServerHandle {

    @Override
    public Observable<Request> requests() {
      return Observable.just(new MockLoginRequest());
    }

    private class MockLoginRequest implements LoginRequest {

      @Override
      public LoginRequestParameters parameters() {
        final LoginRequestParameters parameters = new LoginRequestParameters();
        parameters.accountId = "baijie";
        parameters.password = "baijie";
        return parameters;
      }

      @Override
      public void response(AccountService.LoginResult result) {
        logger.info("MockLoginRequest.response(result: {})", result);
      }
    }
  }

}
