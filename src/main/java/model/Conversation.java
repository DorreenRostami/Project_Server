package model;

import java.io.Serializable;
import java.util.List;

public class Conversation implements Serializable {
    private static final long serialVersionUID = 3L;

    private User starter;
    private Email last;
    private List<Email> messages;

    public Conversation(User starter, Email last, List<Email> messages) {
        this.starter = starter;
        this.last = last;
        this.messages = messages;
    }

    public User getStarter() {
        return starter;
    }

    public Email getLast() {
        return last;
    }

    public void setLast(Email last) {
        this.last = last;
    }

    public List<Email> getMessages() {
        return messages;
    }

    public void setMessages(List<Email> messages) {
        this.messages = messages;
    }

    public void addMessage(Email email) {
        messages.add(email);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Conversation c = (Conversation) o;
        return starter.equals(c.getStarter()) && last.equals(c.getLast());
    }

    @Override
    public int hashCode() {
        return starter.hashCode() + last.hashCode();
    }
}
