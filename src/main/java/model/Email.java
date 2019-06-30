package model;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class Email extends Message implements Serializable {
    private static final long serialVersionUID = 2L;

    private String receiver;
    private String time;
    private String subject = "No Subject";
    private List<FileInfo> filesInfos = null;
    private boolean read = false;
    private boolean imp = false;

    public Email(User sender, String receiver, String subject, String text, List<FileInfo> filesInfos) {
        this.sender = sender;
        this.text = text;
        this.receiver = receiver;
        if (subject.length() > 0)
            this.subject = subject;
        if (filesInfos != null && filesInfos.size() > 0)
            this.filesInfos = filesInfos;
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

    public List<FileInfo> getFilesInfos() {
        return filesInfos;
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

    /**
     * equals method to check if two emails are equal.
     * since a user can't sendComposed two emails at the exact same time checking
     * the equivalence of the time and sender of two emails will be sufficient.
     * @param o another object which will be either equal or not to this email
     * @return true if the sender of time of sending the two emails are the same
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Email e = (Email) o;
        return time.equals(e.getTime()) && sender.equals(e.getSender());
    }

    @Override
    public int hashCode() {
        return time.hashCode() + sender.hashCode();
    }
}