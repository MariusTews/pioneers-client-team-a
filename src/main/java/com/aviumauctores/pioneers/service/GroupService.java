package com.aviumauctores.pioneers.service;

import com.aviumauctores.pioneers.dto.groups.CreateGroupDto;
import com.aviumauctores.pioneers.dto.groups.UpdateGroupDto;
import com.aviumauctores.pioneers.model.Group;
import com.aviumauctores.pioneers.rest.GroupsApiService;
import io.reactivex.rxjava3.core.Observable;

import javax.inject.Inject;
import java.util.List;

public class GroupService {

    private final GroupsApiService groupsApiService;

    @Inject
    public GroupService(GroupsApiService groupsApiService) {
        this.groupsApiService = groupsApiService;
    }

    public Observable<Group> createGroup(List<String> members) {
        return groupsApiService.createGroup(new CreateGroupDto(members));
    }

    public Observable<Group> updateGroup(String id,List<String> members) {
        UpdateGroupDto dto = new UpdateGroupDto(members);
        return groupsApiService.updateGroup(id, new UpdateGroupDto(members));
    }
}
