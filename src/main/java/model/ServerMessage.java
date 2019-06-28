package model;

import java.io.Serializable;

public class ServerMessage extends Message implements Serializable {
    static final long serialVersionUID = 2L;
    private User user;
    private String pass2;

    public User getUser() {
        return user;
    }

    public String getPass2() {
        return pass2;
    }
}