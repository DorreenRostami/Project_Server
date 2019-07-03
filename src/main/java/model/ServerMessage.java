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

    /**
     * for signing up / changing account info / blocking
     *
     * @param messageType signUp / changeInfo/ block or unblock
     * @param sender      the current user
     * @param text        either the second password or the username of the user being blocked/unblocked
     */
    ServerMessage(MessageType messageType, User sender, String text) {
        this(messageType, sender);
        this.text = text;
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