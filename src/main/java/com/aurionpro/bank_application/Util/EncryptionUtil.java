package com.aurionpro.bank_application.Util;

import org.mindrot.jbcrypt.BCrypt;

public class EncryptionUtil {

    public String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    public boolean checkPassword(String password, String hashed) {
        if (password == null || hashed == null) return false;
        try {
            return BCrypt.checkpw(password, hashed);
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

//    public static void main(String[] args) {
//        String rawPassword = "admin123";
//        String hashedPassword = BCrypt.hashpw(rawPassword, BCrypt.gensalt());
//
//        System.out.println("BCrypt Hashed Password: " + hashedPassword);
//    }
}
