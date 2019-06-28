package model;

import java.io.*;
import java.util.List;

public class FileUpdate {
    private static final String DB = "src/main/resources/users/";

    public static void saveUser(User user) throws IOException {
        String path = DB + user.getUsername();

        //make other files if user is new
        if(new File(path).mkdir()) {
            new File(path + "/Inbox.txt").createNewFile();
            new File(path + "/Sent.txt").createNewFile();
        }

        //serialize updated user into file
        ObjectOutputStream ous = new ObjectOutputStream(new FileOutputStream(path + "/info.txt"));
        ous.writeObject(user);
        ous.flush();
        ous.close();
    }

    public static List<Conversation> getMail(ServerMessage message) throws IOException, ClassNotFoundException {
        String path = DB + message.getSender().getUsername() + "/" + message.getMessageType() + ".txt";
        ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path));
        List<Conversation> list = (List<Conversation>) ois.readObject();
        ois.close();
        return list;
    }
}
