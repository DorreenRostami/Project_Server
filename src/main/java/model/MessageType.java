package model;

import java.io.Serializable;

public enum MessageType implements Serializable {
    /**
     * when user first signs in
     */
    signIn,
    /**
     * when user signs out
     */
    signOut,
    /**
     * when user creates a new account
     */
    signUp,
    /**
     * when user is done adding additional info
     */
    makeAccount,
    /**
     * when user wants to view their inbox
     */
    inbox,
    /**
     * when user wants to view the messages they have sent
     */
    sent
}