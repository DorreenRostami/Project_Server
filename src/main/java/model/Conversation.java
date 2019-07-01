package model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Conversation extends Message implements Serializable {
    private static final long serialVersionUID = 3L;

    private List<Email> messages = new ArrayList<>();

    public Conversation(Email first) {
        this.sender = first.getSender();
        messages.add(first);
    }

    @Override
    public User getSender() {
        return sender;
    }

    public List<Email> getMessages() {
        return messages;
    }

    public void addMessage(Email email) {
        messages.add(email);
    }

    public String getTime() {
        return messages.get(messages.size() - 1).getTime();
    }

    @Override
    public String getText() {
        Email last = messages.get(messages.size() - 1);
        return last.getSubject() + " - " + last.getText();
    }

    /**
     * equals method to check if two conversations are equal.
     * since two emails are equal if and only if their sender and time sent are the same,
     * and since a conversation is created when the first email is sent, if the first
     * emails in two conversations are equal, we come to the conclusion that the two
     * conversations are equal / if the user has deleted the first message, it will
     * check the equivalence of the last messages sent in the conversation.
     * @param o another object which will be either equal or not to this conversation
     * @return true if the first email in two conversations are equal
     * @see Email#equals(Object)
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Conversation c = (Conversation) o;
        return (messages.get(0).equals(c.getMessages().get(0)) ||
                messages.get(messages.size() - 1).equals(c.getMessages().get(c.getMessages().size() - 1)));
    }

    @Override
    public int hashCode() {
        return sender.hashCode() + messages.hashCode();
    }
}