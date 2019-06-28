package model;

public class ValidityChecker {
    public static boolean isGoodPassword(String password) {
        boolean upper = false;
        boolean lower = false;
        boolean number = false;
        for (int i = 0; i < password.length(); i++) {
            char c = password.charAt(i);
            if (c >= 'A' && c <= 'Z') {
                upper = true;
            }
            else if (c >= 'a' && c <= 'z') {
                lower = true;
            }
            else if (c >= '0' && c <= '9') {
                number = true;
            }
            else
                return false;
        }
        if (upper && lower && number)
            return true;
        return false;
    }

    public static boolean isGoodUsername(String username) {
        for (int i = 0; i < username.length(); i++) {
            char c = username.charAt(i);
            if (!(((c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z')) || (c >= '0' && c <= '9') || (c == '.')))
                return false;
        }
        return true;
    }
}
