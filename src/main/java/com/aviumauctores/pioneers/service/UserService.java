package com.aviumauctores.pioneers.service;

import javax.inject.Inject;

public class UserService {

    @Inject
    public UserService() {

    }

    public void register(String username, String passwort) {
        //TODO register a new user at the server
        //TODO catch errors from server and inform user
        System.out.print(username + " " + passwort);

    }
}
