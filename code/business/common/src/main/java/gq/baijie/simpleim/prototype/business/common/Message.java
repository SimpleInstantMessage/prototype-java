package gq.baijie.simpleim.prototype.business.common;

import java.util.List;

public class Message {

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
