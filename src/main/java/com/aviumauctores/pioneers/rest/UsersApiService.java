package com.aviumauctores.pioneers.rest;

import com.aviumauctores.pioneers.dto.users.CreateUserDto;
import com.aviumauctores.pioneers.dto.users.UpdateUserDto;
import com.aviumauctores.pioneers.model.User;
import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.*;

import java.util.List;

import static com.aviumauctores.pioneers.Constants.*;

public interface UsersApiService {
    @GET(LIST_USERS_URL)
    Observable<List<User>> listUsers(@Query(QUERY_STATUS) String status, @Query(QUERY_IDS) String ids);

    @POST(CREATE_USER_URL)
    Observable<User> createUser(@Body CreateUserDto createUserDto);

    @GET(GET_USER_URL)
    Observable<User> getUser(@Path(PATH_ID) String id);

    @PATCH(UPDATE_USER_URL)
    Observable<User> updateUser(@Path(PATH_ID) String id, UpdateUserDto updateUserDto);

    @DELETE(DELETE_USER_URL)
    Observable<User> deleteUser(@Path(PATH_ID) String id);

    @GET("users")
    Observable<List<User>> findAll();
}
