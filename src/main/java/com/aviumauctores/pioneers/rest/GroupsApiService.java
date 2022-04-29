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
    Call<List<Group>> listGroups(@Query(QUERY_MEMBERS) String members);

    @POST(CREATE_GROUP_URL)
    Call<Group> createGroup(@Body CreateGroupDto createGroupDto);

    @GET(GET_GROUP_URL)
    Call<Group> getGroup(@Path(PATH_ID) String id);

    @PATCH(UPDATE_GROUP_URL)
    Call<Group> updateGroup(@Path(PATH_ID) String id, @Body UpdateGroupDto updateGroupDto);

    @DELETE(DELETE_GROUP_URL)
    Call<Group> deleteGroup(@Path(PATH_ID) String id);
}
