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
        if(!dir.exists()) {
            dir.mkdir();
        }

        //serialize updated user into file
        ObjectOutputStream ous = new ObjectOutputStream(new FileOutputStream(path + "/Info.txt"));
        ous.writeObject(user);
        ous.flush();
        ous.close();
    }

    public static List<Conversation> getMail(ServerMessage message) throws IOException, ClassNotFoundException {
        String path = DB + message.getSender().getUsername() + "/" + message.getMessageType() + ".txt";
        if (!(new File(path).exists()))
            return null;
        ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path));
        List<Conversation> list = (List<Conversation>) ois.readObject();
        ois.close();
        return list;
    }

    public static void deleteConversation(File file, Conversation conversation) throws IOException, ClassNotFoundException {
        if (file.exists()) {
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file, true));
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file));
            List<Conversation> conversations = (List<Conversation>) ois.readObject();
            ois.close();
            conversations.remove(conversation);
            oos.writeObject(conversations);
            oos.flush();
            oos.close();
        }
    }

    public static void deleteMessage(File file, Conversation conversation, Email email) throws IOException, ClassNotFoundException {
        if (file.exists()) {
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file, true));
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file));
            List<Conversation> conversations = (List<Conversation>) ois.readObject();
            ois.close();
            for (int i = 0; i < conversations.size(); i++) {
                if (conversations.get(i).equals(conversation)) {
                    conversations.get(i).getMessages().remove(email);
                    break;
                }
            }
            oos.writeObject(conversations);
            oos.flush();
            oos.close();
        }
    }

    public static void updateMail(File file, Conversation conversation) throws IOException, ClassNotFoundException {
        List<Conversation> conversations = new ArrayList<>();
        ObjectOutputStream oos;
        ObjectInputStream ois;
        if (!file.exists()) {
            oos = new ObjectOutputStream(new FileOutputStream(file));
            conversations.add(conversation);
        }
        else {
            oos = new ObjectOutputStream(new FileOutputStream(file, true));
            ois = new ObjectInputStream(new FileInputStream(file));
            conversations = (List<Conversation>) ois.readObject();
            ois.close();
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
}
