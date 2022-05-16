package com.aviumauctores.pioneers.service;

import com.aviumauctores.pioneers.model.Member;
import com.aviumauctores.pioneers.rest.GameMembersApiService;
import io.reactivex.rxjava3.core.Observable;

import javax.inject.Inject;
import java.util.List;


public class GameMemberService {

    private final GameMembersApiService gameMembersApiService;
    public final GameService service;


    @Inject
    public GameMemberService(GameMembersApiService gameMembersApiService, GameService service) {
        this.gameMembersApiService = gameMembersApiService;
        this.service = service;
    }

    public Observable<List<Member>> listCurrentGameMembers() {
        return gameMembersApiService.listMembers(service.getCurrentGameID());
    }

    public void deleteGame(){

    }
}
