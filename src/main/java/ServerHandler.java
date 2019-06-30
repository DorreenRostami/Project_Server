
import model.*;

import java.io.*;
import java.net.Socket;
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

            case inbox:
            case sent:
                outputStream.writeObject(FileUpdate.getMail(message));
                break;

            case change:
                new InfoChecker(outputStream).changeInfo(message);
                break;

            case send:
                handle(message.getConversation());
                break;

            case deleteConversation:
                String path = DB + message.getSender().getUsername() + "/Inbox.txt";
                FileUpdate.deleteConversation(new File(path), message.getConversation());
                path = DB + message.getSender().getUsername() + "/Sent.txt";
                FileUpdate.deleteConversation(new File(path), message.getConversation());
                break;

            case deleteMessage:
                path = DB + message.getSender().getUsername() + "/Inbox.txt";
                FileUpdate.deleteMessage(new File(path), message.getConversation(), message.getEmail());
                path = DB + message.getSender().getUsername() + "/Sent.txt";
                FileUpdate.deleteMessage(new File(path), message.getConversation(), message.getEmail());
                break;
        }
        outputStream.flush();
        close();
    }

    private void handle(Conversation conversation) throws IOException, ClassNotFoundException {
        int convoSize = conversation.getMessages().size();
        Email email = conversation.getMessages().get(convoSize - 1);
        String receiver = email.getReceiver();
        String sender = email.getSender().getUsername();

        File receiverFile = new File(DB + receiver);
        if (receiverFile.exists()) {
            receiverFile = new File(DB + receiver + "/Inbox.txt");
            FileUpdate.updateMail(receiverFile, conversation);
            if (convoSize > 1) {
                List<Email> messages = conversation.getMessages();
                boolean receiverSentUpdated = false;
                boolean senderInboxUpdated = false;
                for (Email e : messages) {
                    if (!receiverSentUpdated && receiver.equals(e.getSender().getUsername())) {
                        receiverFile = new File(DB + receiver + "/Sent.txt");
                        FileUpdate.updateMail(receiverFile, conversation);
                        receiverSentUpdated = true;
                    }
                    if (!senderInboxUpdated && sender.equals(e.getReceiver())) {
                        File senderFile = new File(DB + sender + "/Inbox.txt");
                        FileUpdate.updateMail(senderFile, conversation);
                        senderInboxUpdated = true;
                    }
                    if (receiverSentUpdated && senderInboxUpdated)
                        break;
                }
            }
        }
        else {
            File senderFile = new File(DB + sender + "/Inbox.txt");
            email = new Email(new User("mailerdaemon", ""), sender, "Error sending email",
                    "User " + receiver +"@googlemail.com doesn't exist", null);
            FileUpdate.updateMail(senderFile, new Conversation(email));
        }
        File senderFile = new File(DB + sender + "/Sent.txt");
        FileUpdate.updateMail(senderFile, conversation);
    }

    private void close() throws IOException {
        inputStream.close();
        outputStream.close();
        socket.close();
    }
}