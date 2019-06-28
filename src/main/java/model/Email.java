package model;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Email extends Message implements Serializable {
    private static final long serialVersionUID = 2L;

    private String receiver;
    private String time;
    private String subject = "No Subject";
    private byte[] fileBytes;
    private boolean read = false;
    private boolean imp = false;

    public Email(User sender, String receiver, String subject, String text, byte[] fileBytes) {
        this.sender = sender;
        this.text = text;
        this.receiver = receiver;
        if (subject.length() > 0)
            this.subject = subject;
        if (fileBytes.length > 0)
            this.fileBytes = fileBytes;
        this.time = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss").format(new Date());
    }

    public String getReceiver() {
        return receiver;
    }

    public String getTime() {
        return time;
    }

    public String getSubject() {
        return subject;
    }

    public byte[] getFileBytes() {
        return fileBytes;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    public boolean isImp() {
        return imp;
    }

    public void setImp(boolean imp) {
        this.imp = imp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Email e = (Email) o;
        return sender.equals(e.getSender()) && subject.equals(e.getSubject());
    }

    @Override
    public int hashCode() {
        return subject.hashCode() + sender.hashCode();
    }
}