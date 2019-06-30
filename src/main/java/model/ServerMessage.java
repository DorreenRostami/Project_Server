package model;

import java.io.Serializable;

public class ServerMessage extends Message implements Serializable {
    static final long serialVersionUID = 2L;

    private MessageType messageType;
    private Conversation conversation;
    private Email email;

    ServerMessage(MessageType messageType, User sender) {
        this.messageType = messageType;
        this.sender = sender;
    }

    ServerMessage(MessageType messageType, User sender, String pass2) {
        this(messageType, sender);
        this.text = pass2;
    }

    ServerMessage(MessageType messageType, Conversation conversation) {
        this.messageType = messageType;
        this.conversation = conversation;
    }

    ServerMessage(MessageType messageType, Conversation conversation, Email email) {
        this(messageType, conversation);
        this.email = email;
    }

    public MessageType getMessageType() {
        return messageType;
    }

    public Conversation getConversation() {
        return conversation;
    }

    public Email getEmail() {
        return email;
    }
}