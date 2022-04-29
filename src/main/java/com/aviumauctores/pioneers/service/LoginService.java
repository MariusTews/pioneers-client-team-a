package com.aviumauctores.pioneers.service;

import com.aviumauctores.pioneers.dto.auth.LoginDto;
import com.aviumauctores.pioneers.dto.auth.LoginResult;
import com.aviumauctores.pioneers.rest.AuthenticationApiService;
import io.reactivex.rxjava3.core.Observable;

import javax.inject.Inject;

public class LoginService {

    private final AuthenticationApiService authenticationApiService;
    @Inject
    public LoginService(AuthenticationApiService authenticationApiService){

        this.authenticationApiService = authenticationApiService;
    }

    public Observable<LoginResult> login(String username, String password){
        return authenticationApiService.login(new LoginDto(username, password));
    }
}
