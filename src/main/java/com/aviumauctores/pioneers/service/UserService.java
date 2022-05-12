package com.aviumauctores.pioneers.service;

import com.aviumauctores.pioneers.model.User;
import com.aviumauctores.pioneers.rest.UsersApiService;
import io.reactivex.rxjava3.core.Observable;

import com.aviumauctores.pioneers.dto.users.CreateUserDto;


import javax.inject.Inject;
import java.util.List;

public class UserService {

    private final UsersApiService usersApiService;

    @Inject
    public UserService(UsersApiService usersApiService) {
        this.usersApiService = usersApiService;
    }

    public Observable<User> register(String username, String passwort) {
        return usersApiService.createUser(new CreateUserDto(username, passwort));

    }

    public Observable<List<User>> findAll() {
       return this.usersApiService.findAll();
    }

    public Observable<String> getUserName(String id) {
        return usersApiService.getUser(id).map(User::name);
    }


    public Observable<List<User>> listOnlineUsers() {
        return usersApiService.listUsers("online", null);
    }
}
