package model;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class FileUpdate {
    private static final String DB = "src/main/resources/users/";

    public static void saveUser(User user) throws IOException {
        String path = DB + user.getUsername();
        File dir = new File(path);

        //make other files is user is new
        if (dir.mkdir()) {
            ObjectOutputStream o1 = new ObjectOutputStream(new FileOutputStream(path + "/inbox.txt"));
            o1.writeObject(new ArrayList<Conversation>());
            o1.flush();
            o1.close();
            ObjectOutputStream o2 = new ObjectOutputStream(new FileOutputStream(path + "/sent.txt"));
            o2.writeObject(new ArrayList<Conversation>());
            o2.flush();
            o2.close();
            ObjectOutputStream o3 = new ObjectOutputStream(new FileOutputStream(path + "/blocked.txt"));
            o3.writeObject(new ArrayList<String>());
            o3.flush();
            o3.close();

            System.out.println(user.getUsername() + " register " + path + "/picture.png");
            System.out.println(new SimpleDateFormat("dd/MM/yyyy hh:mm:ss").format(new Date()));
        }

        //save image to user's directory
        ByteArrayInputStream bis = new ByteArrayInputStream(user.getImage());
        BufferedImage bImage2 = ImageIO.read(bis);
        ImageIO.write(bImage2, "png", new File(path + "/picture.png") );

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

    public static void addConvToMail(File file, Conversation conversation) throws IOException, ClassNotFoundException {
        ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file));
        List<Conversation> conversations = (List<Conversation>) ois.readObject();
        ois.close();
        ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file));
        conversations.remove(conversation);
        conversations.add(conversation);
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

    public static void handleBlock(MessageType messageType, File file, String user, String blockedUser) throws IOException, ClassNotFoundException {
        ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file));
        List<String> blockedUsers = (List<String>) ois.readObject();
        ois.close();
        ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file));

        if (messageType == MessageType.block) {
            blockedUsers.add(blockedUser);
            System.out.println(user + " block " + blockedUser);
        }
        else {
            blockedUsers.remove(blockedUser);
            System.out.println(user + " unblock " + blockedUser);
        }
        System.out.println(new SimpleDateFormat("dd/MM/yyyy hh:mm:ss").format(new Date()));

        oos.writeObject(blockedUsers);
        oos.flush();
        oos.close();
    }
}
