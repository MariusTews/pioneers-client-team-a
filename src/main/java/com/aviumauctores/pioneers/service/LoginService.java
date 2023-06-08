package com.aviumauctores.pioneers.service;

import com.aviumauctores.pioneers.dto.auth.LoginDto;
import com.aviumauctores.pioneers.dto.auth.LoginResult;
import com.aviumauctores.pioneers.dto.auth.RefreshDto;
import com.aviumauctores.pioneers.rest.AuthenticationApiService;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Observable;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Timer;
import java.util.TimerTask;

@Singleton
public class LoginService {

    private final AuthenticationApiService authenticationApiService;
    private final TokenStorage tokenStorage;
    private final UserService userService;

    private Timer refreshTimer;

    @Inject
    public LoginService(AuthenticationApiService authenticationApiService, TokenStorage tokenStorage, UserService userService) {
        this.authenticationApiService = authenticationApiService;
        this.tokenStorage = tokenStorage;
        this.userService = userService;
    }

    public Observable<LoginResult> login(String username, String password) {
        return authenticationApiService.login(new LoginDto(username, password))
                .doOnNext(this::onSuccessfulLogin);
    }

    public Observable<LoginResult> login(String token) {
        return authenticationApiService.refresh(new RefreshDto(token))
                .doOnNext(this::onSuccessfulLogin);
    }

    private void onSuccessfulLogin(LoginResult result) {
        tokenStorage.setToken(result.accessToken());
        tokenStorage.setRefreshToken(result.refreshToken());
        userService.setCurrentUserID(result._id());
        // Send every 45 minutes a refresh request
        final long delay = 45 * 60 * 1000;
        refreshTimer = new Timer();
        refreshTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                login(tokenStorage.getRefreshToken()).subscribe();
            }
        }, delay);
    }

    public @NonNull Completable logout() {
        return Completable.concatArray(
                userService.changeCurrentUserStatus("offline"),
                authenticationApiService.logout()
                        .doOnComplete(() -> {
                            cancelRefreshTimer();
                            tokenStorage.setToken(null);
                            tokenStorage.setRefreshToken(null);
                            userService.setCurrentUserID(null);
                        })
                        .ignoreElements());
    }

    public void cancelRefreshTimer() {
        if (refreshTimer != null) {
            refreshTimer.cancel();
        }
    }

    public Observable<LoginResult> checkPasswordLogin(String username, String password) {
        return authenticationApiService.login(new LoginDto(username, password));
    }
}
