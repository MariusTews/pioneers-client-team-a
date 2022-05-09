package com.aviumauctores.pioneers.service;

import com.aviumauctores.pioneers.dto.auth.LoginDto;
import com.aviumauctores.pioneers.dto.auth.LoginResult;
import com.aviumauctores.pioneers.rest.AuthenticationApiService;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LoginServiceTest {
    @Spy
    TokenStorage tokenStorage;

    @Mock
    AuthenticationApiService authApiService;

    @InjectMocks
    LoginService loginService;

    @Test
    void login() {
        // Expected result
        LoginResult expResult =
                new LoginResult("42", "Struppi", "online", null, "at", "rt");
        when(authApiService.login(any())).thenReturn(Observable.just(expResult));

        // Actual result
        LoginResult actResult = loginService.login("Struppi", "12345678").blockingFirst();

        assertEquals(actResult, expResult);
        assertEquals(tokenStorage.getToken(), actResult.accessToken());

        // Ensure LoginService.login calls authApiService.login
        verify(authApiService).login(new LoginDto("Struppi", "12345678"));
    }

    @Test
    void logout() {
        when(authApiService.logout()).thenReturn(Observable.empty());

        loginService.logout().blockingAwait();

        // Token should be set to null
        assertNull(tokenStorage.getToken());

        // authApiService.login had to be called
        verify(authApiService).logout();
    }
}