package com.aviumauctores.pioneers.service;

import com.aviumauctores.pioneers.dto.users.CreateUserDto;
import com.aviumauctores.pioneers.model.User;
import com.aviumauctores.pioneers.rest.UsersApiService;
import retrofit2.Call;

public class UserService {

    public void register(String username, String passwort){
        System.out.print(username + " " + passwort);


    }
}
