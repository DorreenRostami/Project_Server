import model.*;

import java.io.*;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class ServerHandler {
    private static final String DB = "src/main/resources/users/";
    private Socket socket;
    private ObjectOutputStream outputStream;
    private ObjectInputStream inputStream;

    ServerHandler(Socket socket, ObjectOutputStream outputStream, ObjectInputStream inputStream) {
        this.socket = socket;
        this.outputStream = outputStream;
        this.inputStream = inputStream;
    }

    ObjectInputStream getInputStream() {
        return inputStream;
    }

    ObjectOutputStream getOutputStream() {
        return outputStream;
    }


    void handle(ServerMessage message) throws IOException, ClassNotFoundException {
        User newUser = message.getSender();
        outputStream.reset();
        List<Conversation> oldList = null;
        switch (message.getMessageType()) {
            case signUp:
                new InfoChecker(outputStream).signUpInfo(message);
                break;

            case makeAccount:
                FileUpdate.saveUser(message.getSender());
                break;

            case signIn:
                String accountPath = DB + newUser.getUsername();
                File file = new File(accountPath);
                if (file.exists()) {
                    file = new File(accountPath + "/info.txt");
                    ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file));
                    User user = (User) ois.readObject();

                    if (!user.getPassword().equals(newUser.getPassword())) {
                        outputStream.writeObject(null);
                        break;
                    }

                    file = new File(accountPath + "/blocked.txt");
                    ois = new ObjectInputStream(new FileInputStream(file));
                    List<String> blockedUsers = (List<String>) ois.readObject();
                    user.setBlockedUsers(blockedUsers);
                    outputStream.writeObject(user);

                    System.out.println(newUser.getUsername() + " sign in");
                    System.out.println(new SimpleDateFormat("dd/MM/yyyy hh:mm:ss").format(new Date()));
                }
                else
                    outputStream.writeObject(null);
                break;

            case inbox:
            case sent:
                outputStream.writeObject(FileUpdate.getMail(message));
                break;

            case changeInfo:
                new InfoChecker(outputStream).changeInfo(message);
                break;

            case send:
                handleSending(message.getConversation());
                break;

            case updateInbox:
                String path = DB + message.getSender().getUsername() + "/inbox.txt";
                ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path));
                oldList = (List<Conversation>) ois.readObject();
                ois.close();
                FileUpdate.updateMail(new File(path), message.getConversations());
                break;
            case updateSent:
                path = DB + message.getSender().getUsername() + "/sent.txt";
                ois = new ObjectInputStream(new FileInputStream(path));
                oldList = (List<Conversation>) ois.readObject();
                ois.close();
                FileUpdate.updateMail(new File(path), message.getConversations());
                break;

            case block:
            case unblock:
                path = DB + message.getSender().getUsername() + "/blocked.txt";
                FileUpdate.handleBlock(message.getMessageType(), new File(path),
                        message.getSender().getUsername(), message.getText());
                break;
        }
        if (message.getMessageType() == MessageType.updateInbox || message.getMessageType() == MessageType.updateSent) {
            String path = DB + message.getSender().getUsername() + "/inbox.txt";
            if (message.getMessageType() == MessageType.updateSent)
                path = DB + message.getSender().getUsername() + "/sent.txt";
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path));
            List<Conversation> newList = (List<Conversation>) ois.readObject();
            ois.close();

            boolean printed = false;
            if (newList.size() == oldList.size()) {
                for (int i = 0; !printed && i < newList.size() ; i++) {
                    List<Email> newEmails = newList.get(i).getMessages();
                    List<Email> oldEmails = oldList.get(i).getMessages();
                    if (newEmails.size() == oldEmails.size()) {
                        String mark = null;
                        for (int j = 0; j < newEmails.size(); j++) {
                            Email newE = newEmails.get(j);
                            Email oldE = oldEmails.get(j);
                            if (newE.isImp() && !oldE.isImp())
                                mark = " unimportant";
                            else if (!newE.isImp() && oldE.isImp())
                                mark = " important";
                            else if (newE.isRead() && !oldE.isRead())
                                mark = " read";
                            else if (!newE.isRead() && oldE.isRead())
                                mark = " unread";
                            else
                                continue;
                            System.out.println(message.getSender().getUsername() + mark);
                            System.out.println("message: " + newE.getSubject() + " " +
                                    newE.getSender().getUsername() + " as" + mark);
                            printed = true;
                            break;
                        }
                    }
                    else {
                        Email deletedmsg = null;
                        for (int j = 0; j < newEmails.size(); j++) {
                            if (!newEmails.get(j).equals(oldEmails.get(j))) {
                                deletedmsg = oldEmails.get(j);
                                break;
                            }
                            else if (j == newEmails.size() - 1) {
                                deletedmsg = oldEmails.get(j + 1);
                            }
                        }
                        System.out.println(message.getSender().getUsername() + " removemsg");
                        System.out.println("message " + deletedmsg.getSubject() + " " + deletedmsg.getSender().getUsername());
                        printed = true;
                        break;
                    }
                }
            }
            else {
                Conversation deletedConv = null;
                for (int i = 0; i < newList.size(); i++) {
                    if (!newList.get(i).equals(oldList.get(i))) {
                        deletedConv = oldList.get(i);
                        break;
                    }
                    else if (i == newList.size() - 1) {
                        deletedConv = oldList.get(i + 1);
                    }
                }
                System.out.println(message.getSender().getUsername() + " removeconv " +
                        deletedConv.getMessages().get(0).getReceiver());
                printed = true;
            }
            if (printed)
                System.out.println(new SimpleDateFormat("dd/MM/yyyy hh:mm:ss").format(new Date()));
        }
        outputStream.flush();
        close();
    }

    private void handleSending(Conversation conversation) throws IOException, ClassNotFoundException {
        List<Email> messages = conversation.getMessages();
        int msgSize = messages.size();

        Conversation receiverConv = new Conversation(new Email(messages.get(0).getSender(), messages.get(0).getReceiver(),
                messages.get(0).getSubject(), messages.get(0).getText(), messages.get(0).getFilesInfos(), messages.get(0).getTime()));
        for (int i = 1 ; i < msgSize; i++) {
            receiverConv.addMessage(new Email(messages.get(i).getSender(), messages.get(i).getReceiver(),
                    messages.get(i).getSubject(), messages.get(i).getText(), messages.get(i).getFilesInfos(), messages.get(i).getTime()));
        }
        receiverConv.getMessages().get(msgSize - 1).setRead(false);

        String sender = messages.get(msgSize - 1).getSender().getUsername();
        String receiver = messages.get(0).getReceiver().equals(sender) ?
                messages.get(0).getSender().getUsername() :  messages.get(0).getReceiver();

        File senderFile = new File(DB + sender + "/sent.txt");
        FileUpdate.addConvToMail(senderFile, conversation);
        if (msgSize > 1) {
            System.out.println(sender + " reply");
            System.out.println("message: " + messages.get(0).getSubject() + " " + DB + sender + "/sent.txt to " + receiver);
        }
        else {
            if (messages.get(0).getText().startsWith("Forwarded message from: ")) {
                int start = 24;
                int end = messages.get(0).getText().indexOf("@googlemail.com\n");
                String initialSender = messages.get(0).getText().substring(start, end);
                System.out.println(sender + " forward");
                System.out.println("message: " + messages.get(0).getSubject() + " " + DB + sender + "/sent.txt from "
                        + initialSender + " to " + receiver);
            }
            else {
                System.out.println(sender + " send");
                System.out.println("message: " + messages.get(0).getSubject() + " " + DB + sender + "/sent.txt to " + receiver);
            }
        }
        System.out.println(new SimpleDateFormat("dd/MM/yyyy hh:mm:ss").format(new Date()));

        File receiverFile = new File(DB + receiver);
        if (receiverFile.exists()) {
            boolean blocked = false;
            File blockedUsersFile = new File(DB + receiver + "/blocked.txt");
            ObjectInputStream blockedUsersIn = new ObjectInputStream(new FileInputStream(blockedUsersFile));
            List<String> blockedUsers = (List<String>) blockedUsersIn.readObject();
            for (String user : blockedUsers) {
                if (user.equals(sender)) {
                    blocked = true;
                    break;
                }
            }

            if (!blocked) {
                receiverFile = new File(DB + receiver + "/inbox.txt");
                FileUpdate.addConvToMail(receiverFile, receiverConv);
                System.out.println(receiver + " receive");
                System.out.println("message: " + sender + " " + DB + receiver + "/inbox.txt");
                System.out.println(new SimpleDateFormat("dd/MM/yyyy hh:mm:ss").format(new Date()));

                boolean receiverSentUpdated = false;
                boolean senderInboxUpdated = false;
                for (Email e : messages) {
                    if (!receiverSentUpdated && receiver.equals(e.getSender().getUsername())) {
                        receiverFile = new File(DB + receiver + "/sent.txt");
                        FileUpdate.addConvToMail(receiverFile, receiverConv);
                        receiverSentUpdated = true;
                    }
                    if (!senderInboxUpdated && sender.equals(e.getReceiver())) {
                        senderFile = new File(DB + sender + "/inbox.txt");
                        FileUpdate.addConvToMail(senderFile, conversation);
                        senderInboxUpdated = true;
                    }
                    if (receiverSentUpdated && senderInboxUpdated)
                        break;
                }
            }
        }
        else {
            senderFile = new File(DB + sender + "/inbox.txt");
            String time = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss").format(new Date());
            Email email = new Email(new User("mailerdaemon", ""), sender, "Error sending email",
                    "User " + receiver +"@googlemail.com doesn't exist", null, time);
            FileUpdate.addConvToMail(senderFile, new Conversation(email));
        }
    }

    private void close() throws IOException {
        inputStream.close();
        outputStream.close();
        socket.close();
    }
}