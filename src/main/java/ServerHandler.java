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

                    file = new File(accountPath + "/blocked.txt");
                    ois = new ObjectInputStream(new FileInputStream(file));
                    List<String> blockedUsers = (List<String>) ois.readObject();
                    user.setBlockedUsers(blockedUsers);
                    outputStream.writeObject(user);
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
                FileUpdate.updateMail(new File(path), message.getConversations());
                break;
            case updateSent:
                path = DB + message.getSender().getUsername() + "/sent.txt";
                FileUpdate.updateMail(new File(path), message.getConversations());
                break;

            case block:
            case unblock:
                path = DB + message.getSender().getUsername() + "/blocked.txt";
                FileUpdate.handleBlock(message.getMessageType(), new File(path), message.getText());
                break;
        }
        outputStream.flush();
        close();
    }

    private void handleSending(Conversation conversation) throws IOException, ClassNotFoundException {
        List<Email> messages = conversation.getMessages();

        Conversation receiverConv = new Conversation(messages.get(0));
        for (int i = 1 ; i < messages.size(); i++) {
            receiverConv.addMessage(new Email(messages.get(i).getSender(), messages.get(i).getReceiver(),
                    messages.get(i).getSubject(), messages.get(i).getText(), messages.get(i).getFilesInfos()));
        }
        for (Email email : receiverConv.getMessages())
            email.setRead(false);

        String sender = messages.get(messages.size() - 1).getSender().getUsername();
        String receiver = messages.get(0).getReceiver().equals(sender) ?
                messages.get(0).getSender().getUsername() :  messages.get(0).getReceiver();

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

                boolean receiverSentUpdated = false;
                boolean senderInboxUpdated = false;
                for (Email e : messages) {
                    if (!receiverSentUpdated && receiver.equals(e.getSender().getUsername())) {
                        receiverFile = new File(DB + receiver + "/sent.txt");
                        FileUpdate.addConvToMail(receiverFile, receiverConv);
                        receiverSentUpdated = true;
                    }
                    if (!senderInboxUpdated && sender.equals(e.getReceiver())) {
                        File senderFile = new File(DB + sender + "/inbox.txt");
                        FileUpdate.addConvToMail(senderFile, conversation);
                        senderInboxUpdated = true;
                    }
                    if (receiverSentUpdated && senderInboxUpdated)
                        break;
                }
            }
        }
        else {
            File senderFile = new File(DB + sender + "/inbox.txt");
            Email email = new Email(new User("mailerdaemon", ""), sender, "Error sending email",
                    "User " + receiver +"@googlemail.com doesn't exist", null);
            FileUpdate.addConvToMail(senderFile, new Conversation(email));
        }

        File senderFile = new File(DB + sender + "/sent.txt");
        FileUpdate.addConvToMail(senderFile, conversation);
    }

    private void close() throws IOException {
        inputStream.close();
        outputStream.close();
        socket.close();
    }
}