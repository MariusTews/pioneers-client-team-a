package com.aviumauctores.pioneers.rest;

import com.aviumauctores.pioneers.dto.groups.CreateGroupDto;
import com.aviumauctores.pioneers.dto.groups.UpdateGroupDto;
import com.aviumauctores.pioneers.model.Group;
import retrofit2.Call;
import retrofit2.http.*;

import java.util.List;

import static com.aviumauctores.pioneers.Constants.*;

public interface GroupsApiService {
    @GET(LIST_GROUPS_URL)
    Call<List<Group>> listGroups(@Header(HEADER_AUTH) String authToken, @Query(QUERY_MEMBERS) String members);

    @POST(CREATE_GROUP_URL)
    Call<Group> createGroup(@Header(HEADER_AUTH) String authToken, @Body CreateGroupDto createGroupDto);

    @GET(GET_GROUP_URL)
    Call<Group> getGroup(@Header(HEADER_AUTH) String authToken, @Path(PATH_ID) String id);

    @PATCH(UPDATE_GROUP_URL)
    Call<Group> updateGroup(@Header(HEADER_AUTH) String authToken, @Path(PATH_ID) String id, @Body UpdateGroupDto updateGroupDto);

    @DELETE(DELETE_GROUP_URL)
    Call<Group> deleteGroup(@Header(HEADER_AUTH) String authToken, @Path(PATH_ID) String id);
}
