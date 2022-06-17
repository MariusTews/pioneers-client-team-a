package com.aviumauctores.pioneers.service;

import com.aviumauctores.pioneers.dto.groups.CreateGroupDto;
import com.aviumauctores.pioneers.dto.groups.UpdateGroupDto;
import com.aviumauctores.pioneers.model.Group;
import com.aviumauctores.pioneers.rest.GroupsApiService;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.CompositeDisposable;

import javax.inject.Inject;
import java.util.List;

public class GroupService {

    private final GroupsApiService groupsApiService;

    @Inject
    public GroupService(GroupsApiService groupsApiService) {
        this.groupsApiService = groupsApiService;
    }

    public Observable<Group> createGroup(List<String> members) {
        return groupsApiService.createGroup(new CreateGroupDto("", members));
    }

    public Observable<Group> getOrCreateGroup(List<String> members, CompositeDisposable disposable) {
        return Observable.create(emitter -> {
            StringBuilder commaSepMembersBuilder = new StringBuilder();
            members.forEach(member -> commaSepMembersBuilder.append(member).append(","));
            commaSepMembersBuilder.deleteCharAt(commaSepMembersBuilder.length() - 1);
            disposable.add(groupsApiService.listGroups(commaSepMembersBuilder.toString())
                    .subscribe(groups -> {
                        if (groups.isEmpty()) {
                            // Create new group
                            disposable.add(createGroup(members).subscribe(emitter::onNext));
                            return;
                        }
                        emitter.onNext(groups.get(0));
                    }));
        });
    }

    public Observable<Group> updateGroup(String id, List<String> members) {

        return groupsApiService.updateGroup(id, new UpdateGroupDto("", members));
    }
}
