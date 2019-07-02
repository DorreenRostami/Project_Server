package model;

import java.io.Serializable;
import java.util.List;

public class ServerMessage extends Message implements Serializable {
    static final long serialVersionUID = 2L;

    private MessageType messageType;
    private Conversation conversation;
    private List<Conversation> conversations;

    ServerMessage(MessageType messageType, User sender) { //for completing sign up (additional info) & signing in & getting lists
        this.messageType = messageType;
        this.sender = sender;
    }

    ServerMessage(MessageType messageType, User sender, String pass2) { //for signing up and changing account info
        this(messageType, sender);
        this.text = pass2;
    }

    ServerMessage(MessageType messageType, Conversation conversation) { //for sending mail
        this.messageType = messageType;
        this.conversation = conversation;
//        this.sender = currentUser.user;
    }

    ServerMessage(MessageType messageType, List<Conversation> conversations) { //for saving list changes
        this.messageType = messageType;
        this.conversations = conversations;
//        this.sender = currentUser.user;
    }

    public MessageType getMessageType() {
        return messageType;
    }

    public Conversation getConversation() {
        return conversation;
    }

    public List<Conversation> getConversations() {
        return conversations;
    }
}