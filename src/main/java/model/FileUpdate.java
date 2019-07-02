package model;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileUpdate {
    private static final String DB = "src/main/resources/users/";

    public static void saveUser(User user) throws IOException {
        String path = DB + user.getUsername();
        File dir = new File(path);

        //make user directory if user is new
        if(!dir.mkdir()) {
            ObjectOutputStream ous = new ObjectOutputStream(new FileOutputStream(path + "/Inbox.txt"));
            ous.writeObject(new ArrayList<Conversation>());
            ous.flush();
            ous = new ObjectOutputStream(new FileOutputStream(path + "/Sent.txt"));
            ous.writeObject(new ArrayList<Conversation>());
            ous.flush();
            ous = new ObjectOutputStream(new FileOutputStream(path + "/Blocked.txt"));
            ous.writeObject(new ArrayList<String>());
            ous.flush();
            ous.close();
        }

        //serialize updated user into file
        ObjectOutputStream ous = new ObjectOutputStream(new FileOutputStream(path + "/Info.txt"));
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

    public static void addConvToMail(File file, Conversation conversation) throws IOException, ClassNotFoundException {
        ObjectOutputStream oos;
        ObjectInputStream ois;
        oos = new ObjectOutputStream(new FileOutputStream(file, true));
        ois = new ObjectInputStream(new FileInputStream(file));
        List<Conversation> conversations = (List<Conversation>) ois.readObject();
        ois.close();
        if (conversations.size() == 0) {
            conversations.add(conversation);
        }
        else {
            for (int i = 0; i < conversations.size(); i++) {
                if (conversation.equals(conversations.get(i))) {
                    conversations.set(i, conversation);
                    break;
                }
                else if (i == conversations.size() - 1)
                    conversations.add(conversation);
            }
        }
        oos.writeObject(conversations);
        oos.flush();
        oos.close();
    }

    public static void updateMail(File file, List<Conversation> list) throws IOException {
        ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file));
        oos.writeObject(list);
        oos.flush();
        oos.close();
    }
}
