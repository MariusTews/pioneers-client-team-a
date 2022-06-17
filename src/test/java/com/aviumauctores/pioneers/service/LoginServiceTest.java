package com.aviumauctores.pioneers.service;

import com.aviumauctores.pioneers.dto.auth.LoginDto;
import com.aviumauctores.pioneers.dto.auth.LoginResult;
import com.aviumauctores.pioneers.dto.auth.RefreshDto;
import com.aviumauctores.pioneers.rest.AuthenticationApiService;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Observable;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoginServiceTest {
    @Spy
    TokenStorage tokenStorage;

    @Mock
    AuthenticationApiService authApiService;

    @InjectMocks
    LoginService loginService;

    @Mock(lenient = true)
    UserService userService;

    @Test
    void login() {
        // Expected result
        LoginResult expResult =
                new LoginResult("42", "Struppi", "online", null, "at", "rt", null);
        when(authApiService.login(any())).thenReturn(Observable.just(expResult));
        doNothing().when(userService).setCurrentUserID(anyString());

        // Actual result
        LoginResult actResult = loginService.login("Struppi", "12345678").blockingFirst();

        assertEquals(actResult, expResult);
        assertEquals(tokenStorage.getToken(), actResult.accessToken());

        // Ensure LoginService.login calls authApiService.login
        verify(authApiService).login(new LoginDto("Struppi", "12345678"));
    }

    @Test
    void loginWithToken() {
        LoginResult expResult =
                new LoginResult("42", "Struppi", "online", null, "at", "rt", null);
        when(authApiService.refresh(any())).thenReturn(Observable.just(expResult));
        doNothing().when(userService).setCurrentUserID(anyString());

        // Actual result
        LoginResult actResult = loginService.login("12345").blockingFirst();

        assertEquals(actResult, expResult);
        assertEquals(tokenStorage.getToken(), actResult.accessToken());

        // Ensure LoginService.login calls authApiService.login
        verify(authApiService).refresh(new RefreshDto("12345"));

    }

    @Test
    void logout() {
        when(authApiService.logout()).thenReturn(Observable.empty());
        doNothing().when(userService).setCurrentUserID(anyString());
        when(userService.changeCurrentUserStatus(anyString())).thenReturn(Completable.complete());

        loginService.logout().blockingAwait();

        // Token should be set to null
        assertNull(tokenStorage.getToken());

        // authApiService.login had to be called
        verify(authApiService).logout();
    }
}