package com.aviumauctores.pioneers.rest;

import com.aviumauctores.pioneers.dto.auth.LoginDto;
import com.aviumauctores.pioneers.dto.auth.LoginResult;
import com.aviumauctores.pioneers.dto.auth.RefreshDto;
import io.reactivex.rxjava3.core.Observable;
import okhttp3.ResponseBody;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

import static com.aviumauctores.pioneers.Constants.*;

public interface AuthenticationApiService {
    @POST(LOGIN_URL)
    Observable<LoginResult> login(@Body LoginDto loginDto);

    @POST(REFRESH_URL)
    Observable<LoginResult> refresh(@Body RefreshDto refreshDto);

    @POST(LOGOUT_URL)
    Observable<ResponseBody> logout();
}
