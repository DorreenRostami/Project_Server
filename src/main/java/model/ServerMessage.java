package main.java.model;

import java.io.File;
import java.io.Serializable;

public class ServerMessage extends Message implements Serializable {
    static final long serialVersionUID = 2L;
    private User user;
    private String pass2;
    private File file;

    public ServerMessage(MessageType messageType, File file) {
        this.messageType = messageType;
        this.file = file;
    }

    public ServerMessage(MessageType messageType, User user) {
        this.messageType = messageType;
        this.user = user;
    }

    public ServerMessage(MessageType messageType, User user, String pass2) {
        this(messageType, user);
        this.pass2 = pass2;
    }

    public User getUser() {
        return user;
    }

    public String getPass2() {
        return pass2;
    }

    public File getFile() {
        return file;
    }
}