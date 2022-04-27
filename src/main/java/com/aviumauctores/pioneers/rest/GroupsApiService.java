package com.aviumauctores.pioneers.rest;

import com.aviumauctores.pioneers.dto.groups.CreateGroupDto;
import com.aviumauctores.pioneers.dto.groups.UpdateGroupDto;
import com.aviumauctores.pioneers.model.Group;
import retrofit2.Call;
import retrofit2.http.*;

import java.util.List;

public interface GroupsApiService {
    @GET("groups")
    Call<List<Group>> listGroups(@Header("Authorization") String authToken, @Query("members") String members);

    @POST("groups")
    Call<Group> createGroup(@Header("Authorization") String authToken, @Body CreateGroupDto createGroupDto);

    @GET("groups/{id}")
    Call<Group> getGroup(@Header("Authorization") String authToken, @Path("id") String id);

    @PATCH("groups/{id}")
    Call<Group> updateGroup(@Header("Authorization") String authToken, @Path("id") String id, @Body UpdateGroupDto updateGroupDto);

    @DELETE("groups/{id}")
    Call<Group> deleteGroup(@Header("Authorization") String authToken, @Path("id") String id);
}
