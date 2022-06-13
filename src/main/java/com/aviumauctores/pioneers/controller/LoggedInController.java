package com.aviumauctores.pioneers.controller;

import com.aviumauctores.pioneers.service.LoginService;
import com.aviumauctores.pioneers.service.UserService;
import io.reactivex.rxjava3.disposables.CompositeDisposable;

public abstract class LoggedInController implements Controller {
    protected final LoginService loginService;
    protected final UserService userService;

    protected CompositeDisposable disposables;

    protected LoggedInController(LoginService loginService, UserService userService) {
        this.loginService = loginService;
        this.userService = userService;
    }

    @Override
    public void destroy(boolean closed) {
        if (closed) {
            loginService.cancelRefreshTimer();
            disposables.add(userService.changeCurrentUserStatus("offline")
                    .subscribe(() -> {
                        if(disposables != null){
                            disposables.dispose();
                            disposables = null;
                        }
                    }));
        } else {
            if (disposables != null) {
                disposables.dispose();
                disposables = null;
            }
        }
    }
}
