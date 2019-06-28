package model;

import java.io.Serializable;
import java.util.Date;

public class Email extends Message implements Serializable {
    private static final long serialVersionUID = 2L;

    private String sender;
    private String receiver;
    private Date date;
    private String subject = "No Subject";
    private String text;
    private int[] fileBytes;
    private boolean read = false;

    public Email(MessageType messageType, String sender, String receiver, String subject, String text, int[] fileBytes) {
        this.messageType = messageType;
        this.sender = sender;
        this.text = text;
        this.receiver = receiver;
        if (subject.length() > 0)
            this.subject = subject;
        if (fileBytes.length > 0)
            this.fileBytes = fileBytes;
        this.date = new Date();
    }

    public String getSender() {
        return sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public Date getDate() {
        return date;
    }

    public String getSubject() {
        return subject;
    }

    public String getText() {
        return text;
    }

    public int[] getFileBytes() {
        return fileBytes;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }
}
