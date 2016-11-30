package gq.baijie.simpleim.prototype.client.javafx.service;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import gq.baijie.simpleim.prototype.business.common.Message;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import rx.Observable;
import rx.subjects.PublishSubject;

import static java.util.stream.Stream.concat;

public class ConversationService {

  //TODO should use Set<Conversation> here
  //final Set<Conversation> conversations = new HashSet<>();
  final ObservableList<Conversation> conversations = FXCollections.observableArrayList();

  public ObservableList<Conversation> getConversations() {
    return conversations;
  }

  @Nullable
  private Conversation findConversation(@Nonnull Set<String> participantIds) {
    return conversations.stream()
        .filter(c -> c.participantIds.equals(participantIds))
        .findAny().orElse(null);
  }

  void logNewMessage(Message message) {
    final Set<String> participantIds =
        concat(Stream.of(message.getSenderId()),
               message.getReceivers().stream().map(Message.Receiver::getReceiverId))
            .collect(Collectors.toSet());
    Conversation conversation = touchConversation(participantIds);
    conversation.addMessage(message);
  }

  private Conversation addNewConversation(@Nonnull Set<String> participantIds) {
    Conversation conversation = new Conversation(participantIds);
    conversations.add(conversation);
    return conversation;
  }

  /**
   * get {@link Conversation} of participantIds, create it if haven't created
   */
  public Conversation touchConversation(@Nonnull Set<String> participantIds) {
    Conversation conversation = findConversation(participantIds);
    if (conversation == null) {
      conversation = addNewConversation(participantIds);
    }
    return conversation;
  }

  public static class Conversation {

    final Set<String> participantIds;
    //TODO memory guard
    final List<Message> messages = new LinkedList<>();
    final PublishSubject<Message> addNewMessageEvents = PublishSubject.create();

    public Conversation(@Nonnull Set<String> participantIds) {
      this.participantIds = Collections.unmodifiableSet(participantIds);
    }

    public Set<String> getParticipantIds() {
      return participantIds;
    }

    public void addMessage(Message message) {
      messages.add(message);
      addNewMessageEvents.onNext(message);
    }

    public List<Message> getMessages() {
      return Collections.unmodifiableList(messages);
    }

    public Observable<Message> getAddNewMessageEvents() {
      return addNewMessageEvents.asObservable();
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) {
        return true;
      }
      if (o == null || getClass() != o.getClass()) {
        return false;
      }
      Conversation that = (Conversation) o;
      return Objects.equals(participantIds, that.participantIds);
    }

    @Override
    public int hashCode() {
      return Objects.hash(participantIds);
    }
  }

}
