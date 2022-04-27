package com.aviumauctores.pioneers.rest;

import com.aviumauctores.pioneers.dto.auth.LoginDto;
import com.aviumauctores.pioneers.dto.auth.LoginResult;
import com.aviumauctores.pioneers.dto.auth.RefreshDto;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface AuthenticationApiService {
    @POST("auth/login")
    Call<LoginResult> login(@Body LoginDto loginDto);

    @POST("auth/refresh")
    Call<LoginResult> refresh(@Body RefreshDto refreshDto);

    @POST("auth/logout")
    Call<ResponseBody> logout(@Header("Authorization:") String authToken);
}
