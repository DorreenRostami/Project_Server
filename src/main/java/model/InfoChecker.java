package model;

import java.io.File;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class InfoChecker {
    private static final String DB = "src/main/resources/users/";
    private boolean error = false;
    private ObjectOutputStream outputStream;

    public InfoChecker(ObjectOutputStream outputStream) {
        this.outputStream = outputStream;
    }

    public void signUpInfo(ServerMessage message) throws IOException {
        User newUser = message.getSender();

        //check if user has entered their full name
        checkName(newUser.getName(), newUser.getSurname());

        //check if birthday is valid
        try {
            DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
            df.setLenient(false);
            df.parse(newUser.getBirthday());
            if(newUser.getAge(newUser.getBirthday()) < 13) {
                outputStream.writeObject(InfoFeedback.young);
                outputStream.flush();
                error = true;
            }
        }
        catch (ParseException e) {
            outputStream.writeObject(InfoFeedback.birthday);
            outputStream.flush();
            error = true;
        }

        //check if password is valid
        checkPass(newUser.getPassword(), message.getText());

        //check if username is valid
        if (newUser.getUsername().length() == 0) {
            outputStream.writeObject(InfoFeedback.enterUsername);
            outputStream.flush();
        }
        else {
            if (!ValidityChecker.isGoodUsername(newUser.getUsername())) {
                outputStream.writeObject(InfoFeedback.badUsername);
                outputStream.flush();
            }
            else if (!error) {
                File file = new File(DB + newUser.getUsername());
                if (!file.exists())
                    outputStream.writeObject(InfoFeedback.signedUp);
                else
                    outputStream.writeObject(InfoFeedback.takenUsername);
                outputStream.flush();
            }
        }
    }

    public void changeInfo(ServerMessage message) throws IOException {
        User newUser = message.getSender();
        checkName(newUser.getName(), newUser.getSurname());
        if (message.getText().length() > 0)
            checkPass(newUser.getPassword(), message.getText());
        if (!error) {
            outputStream.writeObject(InfoFeedback.changed);
            FileUpdate.saveUser(message.getSender());
        }
        outputStream.flush();
    }

    private void checkName(String name, String surname) throws IOException {
        if (name.length() == 0 || surname.length() == 0) {
            outputStream.writeObject(InfoFeedback.fullName);
            outputStream.flush();
            error = true;
        }
    }

    private void checkPass(String pass, String pass2) throws IOException {
        if (pass.length() < 8) {
            outputStream.writeObject(InfoFeedback.shortPass);
            outputStream.flush();
            error = true;
        }
        else {
            if (!ValidityChecker.isGoodPassword(pass)) {
                outputStream.writeObject(InfoFeedback.badPass);
                outputStream.flush();
                error = true;
            }
            else if (!pass.equals(pass2)) {
                outputStream.writeObject(InfoFeedback.mismatchedPass);
                outputStream.flush();
                error = true;
            }
        }
    }
}