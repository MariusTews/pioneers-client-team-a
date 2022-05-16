package com.aviumauctores.pioneers.service;

import com.aviumauctores.pioneers.dto.users.CreateUserDto;
import com.aviumauctores.pioneers.model.User;
import com.aviumauctores.pioneers.rest.UsersApiService;
import io.reactivex.rxjava3.core.Observable;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;

@Singleton
public class UserService {

    private final UsersApiService usersApiService;

    private String currentUserID;

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

    public Observable<List<User>> listOnlineUsers() {
        return usersApiService.listUsers("online", null);
    }

    public Observable<User> getUserByID(String id) {
        return usersApiService.getUser(id);
    }

    public Observable<String> getUserName(String id) {
        return getUserByID(id).map(User::name);
    }

    public String getCurrentUserID() {
        return currentUserID;
    }

    public void setCurrentUserID(String currentUserID) {
        this.currentUserID = currentUserID;
    }
}
