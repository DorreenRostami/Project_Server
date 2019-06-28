package model;

public enum SignUpFeedback {
    /**
     * when the user hasn't entered their full name
     */
    fullName,
    /**
     * when the date the user has entered is invalid
     */
    birthday,
    /**
     * when user is younger than 13
     */
    young,
    /**
     * when password is shorter than 8 characters long
     */
    shortPass,
    /**
     * when password doesn't contain either uppercase or lowercase letters or any numbers
     */
    badPass,
    /**
     * when the passwords entered in 2 fields don't match
     */
    mismatchedPass,
    /**
     * when user hasn't entered a username
     */
    enterUsername,
    /**
     * when username contains characters other than letters, numbers and the ',' character
     */
    badUsername,
    /**
     * when the entered username by the new user is already taken by an old user
     */
    takenUsername,
    /**
     * when there were no errors and the user has successfully signed up
     */
    signedUp
}
