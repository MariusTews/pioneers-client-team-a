package com.aviumauctores.pioneers.service;

import com.aviumauctores.pioneers.TemporaryHttpModule;
import com.aviumauctores.pioneers.rest.AuthenticationApiService;
import com.aviumauctores.pioneers.rest.CheckConnectionApiService;
import io.reactivex.rxjava3.core.Observable;

import javax.inject.Inject;

public class CheckConnectionService {
    private final CheckConnectionApiService checkConnectionApiService;

    @Inject
    public CheckConnectionService() {
        this.checkConnectionApiService = TemporaryHttpModule.checkConnectionApiService();
    }

    public Observable<Object> checkConnection() {
        return checkConnectionApiService.checkConnection();
    }
}
