package com.aviumauctores.pioneers.service;

import com.aviumauctores.pioneers.dto.gamemembers.CreateMemberDto;
import com.aviumauctores.pioneers.dto.gamemembers.UpdateMemberDto;
import com.aviumauctores.pioneers.model.Game;
import com.aviumauctores.pioneers.model.Member;
import com.aviumauctores.pioneers.rest.GameMembersApiService;
import io.reactivex.rxjava3.core.Observable;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;

@Singleton
public class GameMemberService {

    private final GameService gameService;

    private final GameMembersApiService gameMembersApiService;

    private final Observable<Game> game;

    private String gameID;


    @Inject
    public GameMemberService(GameService gameService, GameMembersApiService gameMembersApiService) {
        this.gameService = gameService;
        this.gameMembersApiService = gameMembersApiService;
        this.game = gameService.getCurrentGame();
        this.gameID = this.game.blockingFirst()._id();
    }

    public Observable<Member> createMember(){
        return gameMembersApiService.createMember(gameID, new CreateMemberDto(false, gameService.password));
    }

    public Observable<List<Member>> listCurrentGameMembers() {
        return gameMembersApiService.listMembers(service.getCurrentGameID());
    }
    public Observable<Member> deleteMember(String memberID){
        return gameMembersApiService.deleteMember(gameService.getCurrentGameID(), memberID);

    }

    public Observable<Member> updateMember(String memberID){
        Observable<Member> member = getMember(memberID);
        boolean status = !(member.blockingFirst().ready());
        return gameMembersApiService.updateMember(gameID, memberID, new UpdateMemberDto(status));
    }

    public Observable<Member> getMember(String memberID){
        return gameMembersApiService.getMember(gameID, memberID);
    }
}
