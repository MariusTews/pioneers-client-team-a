package com.aviumauctores.pioneers.rest;

import com.aviumauctores.pioneers.dto.users.CreateUserDto;
import com.aviumauctores.pioneers.dto.users.UpdateUserDto;
import com.aviumauctores.pioneers.model.User;
import retrofit2.Call;
import retrofit2.http.*;

import java.util.List;

import static com.aviumauctores.pioneers.Constants.*;

public interface UsersApiService {
    @GET(LIST_USERS_URL)
    Call<List<User>> listUsers(@Query(QUERY_STATUS) String status, @Query(QUERY_IDS) String ids);

    @POST(CREATE_USER_URL)
    Call<User> createUser(@Body CreateUserDto createUserDto);

    @GET(GET_USER_URL)
    Call<User> getUser(@Path(PATH_ID) String id);

    @PATCH(UPDATE_USER_URL)
    Call<User> updateUser(@Path(PATH_ID) String id, UpdateUserDto updateUserDto);

    @DELETE(DELETE_USER_URL)
    Call<User> deleteUser(@Path(PATH_ID) String id);
}
