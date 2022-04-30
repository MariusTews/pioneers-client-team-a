package com.aviumauctores.pioneers.service;

import com.aviumauctores.pioneers.dto.auth.LoginDto;
import com.aviumauctores.pioneers.dto.auth.LoginResult;
import com.aviumauctores.pioneers.rest.AuthenticationApiService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import javax.inject.Inject;
import java.util.function.Consumer;

public class LoginService {

    private final AuthenticationApiService authenticationApiService;
    @Inject
    public LoginService(AuthenticationApiService authenticationApiService){

        this.authenticationApiService = authenticationApiService;
    }

    public void login(String username, String password, Consumer<? super LoginResult> callback){
        authenticationApiService.login(new LoginDto(username, password)).enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<LoginResult> call, Response<LoginResult> response) {
                callback.accept(response.body());
            }

            @Override
            public void onFailure(Call<LoginResult> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }
}
