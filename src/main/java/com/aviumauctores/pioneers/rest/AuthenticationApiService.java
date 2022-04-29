package com.aviumauctores.pioneers.rest;

import com.aviumauctores.pioneers.dto.auth.LoginDto;
import com.aviumauctores.pioneers.dto.auth.LoginResult;
import com.aviumauctores.pioneers.dto.auth.RefreshDto;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

import javax.inject.Inject;

import static com.aviumauctores.pioneers.Constants.*;

public interface AuthenticationApiService {
    @POST(LOGIN_URL)
    Call<LoginResult> login(@Body LoginDto loginDto);

    @POST(REFRESH_URL)
    Call<LoginResult> refresh(@Body RefreshDto refreshDto);

    @POST(LOGOUT_URL)
    Call<ResponseBody> logout(@Header(HEADER_AUTH) String authToken);
}
