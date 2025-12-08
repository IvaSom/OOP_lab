package ru.ssau.tk.swc.labs.util;

import org.mindrot.jbcrypt.BCrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PasswordHasher {
    private static final Logger logger = LoggerFactory.getLogger(PasswordHasher.class);

    public static String hash(String password) {
        if (password == null || password.isEmpty()) {
            throw new IllegalArgumentException("Password cannot be null or empty");
        }
        String hashed = BCrypt.hashpw(password, BCrypt.gensalt());
        logger.debug("Password hashed successfully");
        return hashed;
    }

    public static boolean verify(String password, String hashed) {
        if (password == null || hashed == null) {
            return false;
        }
        boolean matches = BCrypt.checkpw(password, hashed);
        logger.debug("Password verification result: {}", matches);
        return matches;
    }
}
