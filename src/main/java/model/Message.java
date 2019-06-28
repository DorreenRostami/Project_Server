package model;

import java.io.Serializable;

public abstract class Message implements Serializable {
    static final long serialVersionUID = 2L;

    String text;
    User sender;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }
}
