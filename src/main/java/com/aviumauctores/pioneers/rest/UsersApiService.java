package com.aviumauctores.pioneers.rest;

import com.aviumauctores.pioneers.dto.users.CreateUserDto;
import com.aviumauctores.pioneers.dto.users.UpdateUserDto;
import com.aviumauctores.pioneers.model.User;
import retrofit2.Call;
import retrofit2.http.*;

import java.util.List;

public interface UsersApiService {
    @GET("users")
    Call<List<User>> listUsers(@Header("Authorization") String authToken, @Query("status") String status, @Query("ids") String ids);

    @POST("users")
    Call<User> createUser(@Body CreateUserDto createUserDto);

    @GET("users/{id}")
    Call<User> getUser(@Header("Authorization") String authToken, @Path("id") String id);

    @PATCH("users/{id}")
    Call<User> updateUser(@Header("Authorization") String authToken, @Path("id") String id, UpdateUserDto updateUserDto);

    @DELETE("users/{id}")
    Call<User> deleteUser(@Header("Authorization") String authToken, @Path("id") String id);
}
