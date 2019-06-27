package main.java;

import main.java.model.*;

import java.io.*;
import java.net.Socket;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class ServerHandler {
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


    void handle(ServerMessage message) throws IOException {
        User newUser = message.getUser();
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
                    if (!newUser.getPassword().equals(message.getPass2())) {
                        outputStream.writeObject(SignUpFeedback.mismatchedPass);
                        outputStream.flush();
                        error = true;
                    }
                }

                //check if username is valid
                if (newUser.getUsername().length() == 0) {
                    outputStream.writeObject(SignUpFeedback.enterUsername);
                    outputStream.flush();
                    error = true;
                }
                else {
                    if (!ValidityChecker.isGoodUsername(newUser.getUsername())) {
                        outputStream.writeObject(SignUpFeedback.badUsername);
                        outputStream.flush();
                        error = true;
                    }
                    else if (!error) {
                        File file = new File("src/main/resources/users/" + newUser.getUsername());
                        if (file.mkdir()) {
                            ObjectOutputStream ous = new ObjectOutputStream(
                                    new FileOutputStream(file.getPath() + "/info.txt"));
                            ous.writeObject(newUser);
                            ous.flush();
                            ous.close();
                            outputStream.writeObject(SignUpFeedback.signedUp);
                        }
                        else
                            outputStream.writeObject(SignUpFeedback.takenUsername);
                        outputStream.flush();
                    }
                }
                break;

            case signIn:
                break;
        }
        outputStream.flush();
    }

    void handle (Email email) {
        //TO DO
    }

    void handleImage(Socket socket, String username) {
        try {
            new DBThread(socket, username).start();
        }
        catch (IOException e) {
            e.getMessage();
        }
    }
}

class DBThread extends Thread {
    private static final String DB = "src/main/resources/users/";
    private Socket socket;
    private DataInputStream in;
    private String username;

    public DBThread(Socket socket, String username) throws IOException {
        this.socket = socket;
        in = new DataInputStream(socket.getInputStream());
        this.username = username;
    }

    @Override
    public void run() {
        try {
            byte[] buffer = in.readAllBytes();
            File userFile = new File(DB + username + "/info.txt");
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(userFile));
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(userFile));
            User user = (User) ois.readObject();
            user.setImage(buffer);
            oos.writeObject(user);

            ois.close();
            oos.close();
            in.close();
            socket.close();
        }
        catch (IOException | ClassNotFoundException e) {
            e.getMessage();
        }
    }
}