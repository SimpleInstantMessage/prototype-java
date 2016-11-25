package gq.baijie.simpleim.prototype.client.javafx.service;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class ConversationService {

  //TODO should use Set<Conversation> here
  //final Set<Conversation> conversations = new HashSet<>();
  final ObservableList<Conversation> conversations = FXCollections.observableArrayList();

  public ObservableList<Conversation> getConversations() {
    return conversations;
  }

  @Nullable
  Conversation findConversation(@Nonnull Set<String> participantIds) {
    return conversations.stream()
        .filter(c -> c.participantIds.equals(participantIds))
        .findAny().orElse(null);
  }

  void logNewMessage(ChatService.Message message) {
    final Set<String> participantIds = message.getReceivers().stream()
        .map(ChatService.Message.Receiver::getReceiverId)
        .collect(Collectors.toSet());
    Conversation conversation = touchConversation(participantIds);
    conversation.getMessages().add(message);
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
    final List<ChatService.Message> messages = new LinkedList<>();

    public Conversation(@Nonnull Set<String> participantIds) {
      this.participantIds = Collections.unmodifiableSet(participantIds);
    }

    public Conversation(String... participantIds) {
      this(Arrays.stream(participantIds).collect(Collectors.toSet()));
    }


    public Set<String> getParticipantIds() {
      return participantIds;
    }

    public List<ChatService.Message> getMessages() {
      return messages;
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
