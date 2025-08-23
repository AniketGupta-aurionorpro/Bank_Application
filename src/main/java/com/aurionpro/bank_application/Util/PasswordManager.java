package com.aurionpro.bank_application.Util;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordManager {

    public String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    public boolean checkPassword(String password, String hashed) {
        return BCrypt.checkpw(password, hashed);
    }

//    public static void main(String[] args) {
//        String rawPassword = "admin123";
//        String hashedPassword = BCrypt.hashpw(rawPassword, BCrypt.gensalt());
//
//        System.out.println("BCrypt Hashed Password: " + hashedPassword);
//    }
}
