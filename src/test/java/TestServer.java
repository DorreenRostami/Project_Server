import model.*;
import org.junit.*;
import org.apache.commons.io.FileUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.List;

import static org.junit.Assert.*;

public class TestServer {
    private static final String DB = "src/main/resources/users/";
    private static Connection connection;
    private static User user1 = new User("name1", "surname1", "1/1/2000", "test1", "123456aA");
    private static User user2 = new User("name2", "surname2", "1/1/2000", "test2", "123456aA");

    @BeforeClass
    public static void init() throws IOException {
        BufferedImage bImage = ImageIO.read(new File("src/main/resources/images/avatar.png"));
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ImageIO.write(bImage, "png", bos);
        byte [] data = bos.toByteArray();
        bos.close();

        Server.start();
        connection = new Connection();
        connection.signUpConnection(user1, "123456aA");
        user1.setImage(data);
        user1.setGender(Gender.Female);
        user1.setMobile("1234567890");
        connection = new Connection();
        connection.signUpConnection(user1);
        connection = new Connection();
        connection.signUpConnection(user2, "123456aA");
        user2.setImage(data);
        user2.setGender(Gender.Male);
        connection = new Connection();
        connection.signUpConnection(user2);
    }

    @AfterClass
    public static void delete() throws IOException {
        FileUtils.deleteDirectory(new File(DB + "test1"));
        FileUtils.deleteDirectory(new File(DB + "test2"));
        assertFalse(new File(DB + "test1").exists());
        assertFalse(new File(DB + "test2").exists());
    }

    @Before
    public void before() throws IOException {
        connection = new Connection();
    }

    @Test
    public void testInvalidUsers() throws IOException {
        User user = new User("", "", "", "", "");
        connection = new Connection();
        List<InfoFeedback> feedback = connection.signUpConnection(user, "");
        assertTrue(feedback.contains(InfoFeedback.fullName));
        assertTrue(feedback.contains(InfoFeedback.birthday));
        assertTrue(feedback.contains(InfoFeedback.shortPass));
        assertTrue(feedback.contains(InfoFeedback.enterUsername));

        user = new User("a", "a", "1/1/2018", "a#", "12345678");
        connection = new Connection();
        feedback = connection.signUpConnection(user, "");
        assertTrue(feedback.contains(InfoFeedback.young));
        assertTrue(feedback.contains(InfoFeedback.badPass));
        assertTrue(feedback.contains(InfoFeedback.badUsername));

        user = new User("a", "a", "1/1/2000", "a", "12345678aA");
        connection = new Connection();
        feedback = connection.signUpConnection(user, "a");
        assertTrue(feedback.contains(InfoFeedback.mismatchedPass));

        user = new User("a", "a", "1/1/2000", "test1", "12345678aA");
        connection = new Connection();
        feedback = connection.signUpConnection(user, "12345678aA");
        assertTrue(feedback.contains(InfoFeedback.takenUsername));
    }

    @Test
    public void testComposing() throws IOException, ClassNotFoundException, InterruptedException {
        currentUser.user = user1;
        Email email = new Email(user1, user2.getUsername(), "test composing", "test", null);
        connection.sendMail(new Conversation(email));
        synchronized (ServerRunner.threadCondition) {
            ServerRunner.threadCondition.wait();
        }
        connection = new Connection();
        List<Conversation> list = connection.getList(MessageType.sent);
        assertTrue(list.contains(new Conversation(email)));

        ObjectInputStream ois = new ObjectInputStream(new FileInputStream(DB + "test1/sent.txt"));
        List<Conversation> list1 = (List<Conversation>) ois.readObject();
        ois.close();
        assertTrue(list1.contains(new Conversation(email)));

        ois = new ObjectInputStream(new FileInputStream(DB + "test2/inbox.txt"));
        List<Conversation> list2 = (List<Conversation>) ois.readObject();
        ois.close();
        assertTrue(list2.contains(new Conversation(email)));
    }

    @Test
    public void testBlock() throws InterruptedException, IOException, ClassNotFoundException {
        currentUser.user = user1;
        connection.handleBlock(MessageType.block, user2.getUsername());
        synchronized (ServerRunner.threadCondition) {
            ServerRunner.threadCondition.wait();
        }
        ObjectInputStream ois = new ObjectInputStream(new FileInputStream(DB + "test1/blocked.txt"));
        List<String> list = (List<String>) ois.readObject();
        ois.close();
        assertTrue(list.contains(user2.getUsername()));

        connection = new Connection();
        connection.handleBlock(MessageType.unblock, user2.getUsername());
        synchronized (ServerRunner.threadCondition) {
            ServerRunner.threadCondition.wait();
        }
        ois = new ObjectInputStream(new FileInputStream(DB + "test1/blocked.txt"));
        list = (List<String>) ois.readObject();
        ois.close();
        assertFalse(list.contains(user2.getUsername()));
    }
}