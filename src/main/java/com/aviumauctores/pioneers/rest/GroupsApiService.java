package com.aviumauctores.pioneers.rest;

import com.aviumauctores.pioneers.dto.groups.CreateGroupDto;
import com.aviumauctores.pioneers.dto.groups.UpdateGroupDto;
import com.aviumauctores.pioneers.model.Group;
import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.*;

import java.util.List;

import static com.aviumauctores.pioneers.Constants.*;

public interface GroupsApiService {
    @GET(LIST_GROUPS_URL)
    Observable<List<Group>> listGroups(@Query(QUERY_MEMBERS) String members);

    @POST(CREATE_GROUP_URL)
    Observable<Group> createGroup(@Body CreateGroupDto createGroupDto);

    @PATCH(UPDATE_GROUP_URL)
    Observable<Group> updateGroup(@Path(PATH_ID) String id, @Body UpdateGroupDto updateGroupDto);

}
