
import model.*;

import java.io.*;
import java.net.Socket;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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

    public ObjectInputStream getInputStream() {
        return inputStream;
    }

    public ObjectOutputStream getOutputStream() {
        return outputStream;
    }


    void handle(ServerMessage message) throws IOException, ClassNotFoundException {
        User newUser = message.getSender();
        switch (message.getMessageType()) {
            case signUp:
                outputStream.reset();
                boolean error = false;

                //check if user has entered their full name
                if (newUser.getName().length() == 0 || newUser.getSurname().length() == 0) {
                    outputStream.writeObject(SignUpFeedback.fullName);
                    outputStream.flush();
                    error = true;
                }

                //check if birthday is valid
                try {
                    DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
                    df.setLenient(false);
                    df.parse(newUser.getBirthday());
                    if(newUser.getAge(newUser.getBirthday()) < 13) {
                        outputStream.writeObject(SignUpFeedback.young);
                        outputStream.flush();
                        error = true;
                    }
                }
                catch (ParseException e) {
                    outputStream.writeObject(SignUpFeedback.birthday);
                    outputStream.flush();
                    error = true;
                }

                //check if password is valid
                if (newUser.getPassword().length() < 8) {
                    outputStream.writeObject(SignUpFeedback.shortPass);
                    outputStream.flush();
                    error = true;
                }
                else {
                    if (!ValidityChecker.isGoodPassword(newUser.getPassword())) {
                        outputStream.writeObject(SignUpFeedback.badPass);
                        outputStream.flush();
                        error = true;
                    }
                    if (!newUser.getPassword().equals(message.getText())) {
                        outputStream.writeObject(SignUpFeedback.mismatchedPass);
                        outputStream.flush();
                        error = true;
                    }
                }

                //check if username is valid
                if (newUser.getUsername().length() == 0) {
                    outputStream.writeObject(SignUpFeedback.enterUsername);
                    outputStream.flush();
                }
                else {
                    if (!ValidityChecker.isGoodUsername(newUser.getUsername())) {
                        outputStream.writeObject(SignUpFeedback.badUsername);
                        outputStream.flush();
                    }
                    else if (!error) {
                        File file = new File(DB + newUser.getUsername());
                        if (!file.exists())
                            outputStream.writeObject(SignUpFeedback.signedUp);
                        else
                            outputStream.writeObject(SignUpFeedback.takenUsername);
                        outputStream.flush();
                    }
                }
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
        }
        outputStream.flush();
        close();
    }

    void handle (Conversation conversation) throws IOException, ClassNotFoundException {
        int convoSize = conversation.getMessages().size();
        Email email = conversation.getMessages().get(convoSize - 1);
        String receiver = email.getReceiver();
        String sender = email.getSender().getUsername();

        File receiverFile = new File(DB + receiver);
        if (receiverFile.exists()) {
            receiverFile = new File(DB + receiver + "/Inbox.txt");
            FileUpdate.updateMail(receiverFile, conversation);
            if (convoSize != 1) {
                List<Email> messages = conversation.getMessages();
                boolean recieverSentUpdated = false;
                boolean senderInboxUpdated = false;
                for (Email e : messages) {
                    if (!recieverSentUpdated && receiver.equals(e.getSender().getUsername())) {
                        receiverFile = new File(DB + receiver + "/Sent.txt");
                        FileUpdate.updateMail(receiverFile, conversation);
                        recieverSentUpdated = true;
                    }
                    if (!senderInboxUpdated && sender.equals(e.getReceiver())) {
                        File senderFile = new File(DB + sender + "/Inbox.txt");
                        FileUpdate.updateMail(senderFile, conversation);
                        senderInboxUpdated = true;
                    }
                    if (recieverSentUpdated && senderInboxUpdated)
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