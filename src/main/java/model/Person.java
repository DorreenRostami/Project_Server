package main.java.model;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public interface Person extends Serializable {
    long serialVersionUID = 1L;

    void setName(String name);
    String getName();
    void setSurname(String surname);
    String getSurname();
    void setBirthday(String str);
    String getBirthday();

    default public int getAge(String birthday) throws ParseException {
        long yearMilliSeconds = 1000L * 365 * 24 * 60 * 60;
        long currentYear = new Date().getTime() / yearMilliSeconds;
        Date birthDate = new SimpleDateFormat("dd/MM/yyyy").parse(birthday);
        long birthYear = birthDate.getTime() / yearMilliSeconds;
        return (int)(currentYear - birthYear);
    }
}