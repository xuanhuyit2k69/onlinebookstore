package vn.edu.xyz.olms.util;

import java.util.regex.Pattern;

public final class PasswordValidator {

    private static final Pattern HAS_UPPER = Pattern.compile("[A-Z]");
    private static final Pattern HAS_DIGIT = Pattern.compile("[0-9]");

    private PasswordValidator() {}

    public static boolean isValid(String password) {
        if (password == null || password.length() < 8) {
            return false;
        }
        return HAS_UPPER.matcher(password).find() && HAS_DIGIT.matcher(password).find();
    }
}
