package model;

import java.io.Serializable;
import java.util.List;

public class ServerMessage extends Message implements Serializable {
    static final long serialVersionUID = 2L;

    private MessageType messageType;
    private Conversation conversation;
    private List<Conversation> conversations;

    public ServerMessage(MessageType messageType, User sender) { //for completing sign up (additional info) & signing in & getting lists
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
    public ServerMessage(MessageType messageType, User sender, String text) {
        this(messageType, sender);
        this.text = text;
    }

    public ServerMessage(MessageType messageType, Conversation conversation, User sender) { //for sending mail
        this.messageType = messageType;
        this.conversation = conversation;
        this.sender = sender;
    }

    public ServerMessage(MessageType messageType, List<Conversation> conversations, User sender) { //for saving list changes
        this.messageType = messageType;
        this.conversations = conversations;
        this.sender = sender;
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