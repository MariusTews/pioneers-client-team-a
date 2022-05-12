package com.aviumauctores.pioneers.service;

import com.aviumauctores.pioneers.dto.auth.LoginDto;
import com.aviumauctores.pioneers.dto.auth.LoginResult;
import com.aviumauctores.pioneers.rest.AuthenticationApiService;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Observable;

import javax.inject.Inject;

public class LoginService {

    private final AuthenticationApiService authenticationApiService;
    private final TokenStorage tokenStorage;
    private final UserService userService;

    @Inject
    public LoginService(AuthenticationApiService authenticationApiService, TokenStorage tokenStorage, UserService userService){
        this.authenticationApiService = authenticationApiService;
        this.tokenStorage = tokenStorage;
        this.userService = userService;
    }

    public Observable<LoginResult> login(String username, String password){
        return authenticationApiService.login(new LoginDto(username, password))
                .doOnNext(result -> {
                    tokenStorage.setToken(result.accessToken());
                    userService.setCurrentUserID(result._id());
                });
    }

    public @NonNull Completable logout() {
        return authenticationApiService.logout()
                .doOnComplete(() -> {
                    tokenStorage.setToken(null);
                    userService.setCurrentUserID(null);
                })
                .ignoreElements();
    }
}
