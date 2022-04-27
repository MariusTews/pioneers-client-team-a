package com.aviumauctores.pioneers.service;

public class LoginService {

    public void login(String username, String password){
        System.out.println("Login" + username + " " + "*".repeat(password.length()));
    }
}
