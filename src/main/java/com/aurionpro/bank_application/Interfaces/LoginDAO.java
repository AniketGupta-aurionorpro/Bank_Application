package com.aurionpro.bank_application.Interfaces;

import com.aurionpro.bank_application.Models.User;

public interface LoginDAO {

    public User getUserByUsername(String username);
}
