package gq.baijie.simpleim.prototype.client.javafx.service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Observable;
import rx.subjects.PublishSubject;

@Singleton
public class ChatService {

  private final PublishSubject<Message> newMessageEventBus = PublishSubject.create();

  @Inject
  public ChatService() {
  }

  Observable<Message> newMessageEventBus() {
    return newMessageEventBus.asObservable();
  }

  Message sendMessage(String senderId, String message, Set<String> receiverIds) {
    final List<Message.Receiver> receivers = receiverIds.stream()
        .map(Message.Receiver::new)
        .collect(Collectors.toList());
    final Message newMessage = new Message(senderId, receivers, message);
    newMessageEventBus.onNext(newMessage);
    return newMessage;
  }

  public static class Message {

    final String senderId;
    final List<Receiver> receivers;
    final String message;

    public Message(String senderId, List<Receiver> receivers, String message) {
      this.senderId = senderId;
      this.receivers = receivers;
      this.message = message;
    }

    public String getSenderId() {
      return senderId;
    }

    public List<Receiver> getReceivers() {
      return receivers;
    }

    public String getMessage() {
      return message;
    }

    public static class Receiver {

      final String receiverId;
      ReceiveState receiveState = ReceiveState.UNSEND;

      public Receiver(String receiverId) {
        this.receiverId = receiverId;
      }

      public String getReceiverId() {
        return receiverId;
      }

      public ReceiveState getReceiveState() {
        return receiveState;
      }

      public void setReceiveState(
          ReceiveState receiveState) {
        this.receiveState = receiveState;
      }

      public enum ReceiveState {
        UNSEND,
        SENDING,
        RECEIVED,
        // READED,
        SEND_FAILED
      }
    }
  }

}
