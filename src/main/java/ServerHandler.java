
import model.*;

import java.io.*;
import java.net.Socket;
import java.util.Collections;
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


    void handleSending(ServerMessage message) throws IOException, ClassNotFoundException {
        User newUser = message.getSender();
        switch (message.getMessageType()) {
            case signUp:
                outputStream.reset();
                new InfoChecker(outputStream).signUpInfo(message);
                break;

            case signIn:
                String accountPath = DB + newUser.getUsername();
                File account = new File(accountPath);
                if (account.exists()) {
                    account = new File(account + "/Info.txt");
                    ObjectInputStream ois = new ObjectInputStream(new FileInputStream(account));
                    User user = (User) ois.readObject();
                    outputStream.writeObject(user);
                }
                else
                    outputStream.writeObject(null);
                break;

            case makeAccount:
                FileUpdate.saveUser(message.getSender());
                break;

            case getInbox:
            case getSent:
                outputStream.writeObject(FileUpdate.getMail(message));
                break;

            case changeInfo:
                new InfoChecker(outputStream).changeInfo(message);
                break;

            case send:
                handleSending(message.getConversation());
                break;

            case updateInbox:
                String path = DB + message.getSender().getUsername() + "/Inbox.txt";
                FileUpdate.updateMail(new File(path), message.getConversations());
                break;
            case updateSent:
                path = DB + message.getSender().getUsername() + "/Sent.txt";
                FileUpdate.updateMail(new File(path), message.getConversations());
        }
        outputStream.flush();
        close();
    }

    private void handleSending(Conversation conversation) throws IOException, ClassNotFoundException {
        List<Email> messages = conversation.getMessages();

        Conversation receiverConv = new Conversation(messages.get(0));
        for (int i = 1 ; i < messages.size(); i++)
            receiverConv.addMessage(messages.get(i));
        for (Email email : receiverConv.getMessages())
            email.setRead(false);

        String sender = messages.get(messages.size() - 1).getSender().getUsername();
        String receiver = "";
        for (Email email : messages) {
            if (!email.getSender().getUsername().equals(sender))
                receiver = email.getSender().getUsername();
        }

        File receiverFile = new File(DB + receiver);
        if (receiverFile.exists()) {
            receiverFile = new File(DB + receiver + "/Inbox.txt");
            FileUpdate.addConvToMail(receiverFile, receiverConv);

            boolean receiverSentUpdated = false;
            boolean senderInboxUpdated = false;
            for (Email e : messages) {
                if (!receiverSentUpdated && receiver.equals(e.getSender().getUsername())) {
                    receiverFile = new File(DB + receiver + "/Sent.txt");
                    FileUpdate.addConvToMail(receiverFile, receiverConv);
                    receiverSentUpdated = true;
                }
                if (!senderInboxUpdated && sender.equals(e.getReceiver())) {
                    File senderFile = new File(DB + sender + "/Inbox.txt");
                    FileUpdate.addConvToMail(senderFile, conversation);
                    senderInboxUpdated = true;
                }
                if (receiverSentUpdated && senderInboxUpdated)
                    break;
            }
        }
        else {
            File senderFile = new File(DB + sender + "/Inbox.txt");
            Email email = new Email(new User("mailerdaemon", ""), sender, "Error sending email",
                    "User " + receiver +"@googlemail.com doesn't exist", null);
            FileUpdate.addConvToMail(senderFile, new Conversation(email));
        }
        File senderFile = new File(DB + sender + "/Sent.txt");
        FileUpdate.addConvToMail(senderFile, conversation);
    }

    private void close() throws IOException {
        inputStream.close();
        outputStream.close();
        socket.close();
    }
}