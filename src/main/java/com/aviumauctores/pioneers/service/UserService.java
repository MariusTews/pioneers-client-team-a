package com.aviumauctores.pioneers.service;

import com.aviumauctores.pioneers.model.User;
import com.aviumauctores.pioneers.rest.UsersApiService;
import io.reactivex.rxjava3.core.Observable;

import javax.inject.Inject;
import java.util.List;

public class UserService {

    private final UsersApiService usersApiService;

    @Inject
    public UserService(UsersApiService usersApiService) {
        this.usersApiService = usersApiService;
    }

    public void register(String username, String passwort) {
        //TODO register a new user at the server
        //TODO catch errors from server and inform user
        System.out.print(username + " " + passwort);

    }

    public Observable<List<User>> findAll() {
       return this.usersApiService.findAll();
    }

    public Observable<String> getUserName(String id) {
        return usersApiService.getUser(id).map(User::name);
    }
}
