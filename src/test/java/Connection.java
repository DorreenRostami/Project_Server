import model.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Connection {
    private static final int requestPort = 8080;
    private static final String serverIP = "localhost";

    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;

    public Connection() throws IOException {
        socket = new Socket(serverIP, requestPort);
        out = new ObjectOutputStream(socket.getOutputStream());
        in = new ObjectInputStream(socket.getInputStream());
    }

    public List<InfoFeedback> settingsConnection(User user, String pass2) {
        List<InfoFeedback> messageList = new ArrayList<>();
        try {
            out.writeObject(new ServerMessage(MessageType.changeInfo, user, pass2));
            out.flush();
            InfoFeedback str = null;
            while (str != InfoFeedback.changed) {
                str = (InfoFeedback) in.readObject();
                messageList.add(str);
            }
            terminate();
        }
        catch (IOException | ClassNotFoundException e) {
            e.getMessage();
        }
        return messageList;
    }

    public void signUpConnection(User user) {
        try {
            out.writeObject(new ServerMessage(MessageType.makeAccount, user));
            out.flush();
            terminate();
        }
        catch (IOException e) {
            e.getMessage();
        }
    }

    public List<InfoFeedback> signUpConnection (User user, String pass2) {
        List<InfoFeedback> messageList = new ArrayList<>();
        try {
            out.writeObject(new ServerMessage(MessageType.signUp, user, pass2));
            out.flush();
            InfoFeedback str = null;
            while (str != InfoFeedback.signedUp) {
                str = (InfoFeedback) in.readObject();
                messageList.add(str);
            }
            terminate();
        }
        catch (IOException | ClassNotFoundException e) {
            e.getMessage();
        }
        return messageList;
    }

    public boolean signInConnection (String username, String password) {
        try {
            out.writeObject(new ServerMessage(MessageType.signIn, new User(username, password)));
            out.flush();
            User u = (User) in.readObject();
            terminate();
            if (u != null) {
                currentUser.user = u;
                return true;
            }
        }
        catch (IOException | ClassNotFoundException e) {
            e.getMessage();
        }
        return false;
    }

    public List<Conversation> getList (MessageType listType) {
        List<Conversation> list = null;
        try {
            out.writeObject(new ServerMessage(listType, currentUser.user));
            out.flush();
            list = (List<Conversation>) in.readObject();
            terminate();
        }
        catch (IOException | ClassNotFoundException e) {
            e.getMessage();
        }
        return list;
    }

    /**
     * sends the conversation which contains the new email to the server.
     * @param conversation the conversation containing the new email
     */
    public void sendMail(Conversation conversation) {
        try {
            out.writeObject(new ServerMessage(MessageType.send, conversation, currentUser.user));
            out.flush();
            terminate();
        }
        catch (IOException e) {
            e.getMessage();
        }
    }

    public void saveListChanges(MessageType messageType, List<Conversation> list) {
        try {
            out.writeObject(new ServerMessage(messageType, list, currentUser.user));
            out.flush();
            terminate();
        }
        catch (IOException e) {
            e.getMessage();
        }
    }

    public void handleBlock(MessageType messageType, String blockedUser) {
        try {
            out.writeObject(new ServerMessage(messageType, currentUser.user, blockedUser));
            out.flush();
            terminate();
        }
        catch (IOException e) {
            e.getMessage();
        }
    }

    private void terminate() throws IOException {
        in.close();
        out.close();
        socket.close();
    }
}