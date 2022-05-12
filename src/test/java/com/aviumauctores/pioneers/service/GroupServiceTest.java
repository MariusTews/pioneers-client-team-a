package com.aviumauctores.pioneers.service;

import com.aviumauctores.pioneers.dto.groups.CreateGroupDto;
import com.aviumauctores.pioneers.dto.groups.UpdateGroupDto;
import com.aviumauctores.pioneers.model.Group;
import com.aviumauctores.pioneers.rest.GroupsApiService;
import io.reactivex.rxjava3.core.Observable;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GroupServiceTest {

    @Mock
    GroupsApiService groupsApiService;

    @InjectMocks
    GroupService groupService;

    @Test
    void createGroup() {
        List<String> members = new ArrayList<>();
        members.add("4");
        Group group = new Group("1","2","3", members);
        when(groupsApiService.createGroup(any())).thenReturn(Observable.just(group));

        // check if the group will be created correctly
        Group gr = groupService.createGroup(members).blockingFirst();
        assertEquals(gr, group);

        verify(groupsApiService).createGroup(new CreateGroupDto(members));

    }

    @Test
    void updateGroup() {
        // create a Group
        List<String> members = new ArrayList<>();
        members.add("4");
        Group group = new Group("1","2","3", members);
        // create an updateGroup
        List<String> updateMembers = new ArrayList<>();
        updateMembers.add("4");
        updateMembers.add("5");
        Group updateGroup = new Group("1","2","3", updateMembers);
        when(groupsApiService.updateGroup(any(), any())).thenReturn(Observable.just(updateGroup));

        //check if group will be updated correctly
        Group gr = groupService.updateGroup(group._id(), members).blockingFirst();
        assertEquals(gr, updateGroup);

        verify(groupsApiService).updateGroup("3", new UpdateGroupDto(members));

    }

}