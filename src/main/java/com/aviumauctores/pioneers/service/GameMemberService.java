package com.aviumauctores.pioneers.service;

import com.aviumauctores.pioneers.dto.gamemembers.CreateMemberDto;
import com.aviumauctores.pioneers.dto.gamemembers.UpdateMemberDto;
import com.aviumauctores.pioneers.model.Game;
import com.aviumauctores.pioneers.model.Member;
import com.aviumauctores.pioneers.rest.GameMembersApiService;
import io.reactivex.rxjava3.core.Observable;
import javafx.scene.paint.Color;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;

@Singleton
public class GameMemberService {

    private final GameService gameService;

    private final GameMembersApiService gameMembersApiService;


    private String gameID;


    @Inject
    public GameMemberService(GameService gameService, GameMembersApiService gameMembersApiService) {
        this.gameService = gameService;
        this.gameMembersApiService = gameMembersApiService;
        this.gameID = gameService.getCurrentGameID();
    }

    public Observable<Member> createMember(){
        return gameMembersApiService.createMember(gameID, new CreateMemberDto(false, gameService.password,null));
    }

    public Observable<List<Member>> listCurrentGameMembers() {
        return gameMembersApiService.listMembers(gameService.getCurrentGameID());
    }
    public Observable<Member> deleteMember(String memberID){
        return gameMembersApiService.deleteMember(gameService.getCurrentGameID(), memberID);

    }

    public Observable<Member> updateMember(String memberID, String color){
        Observable<Member> member = getMember(memberID);
        boolean status = !(member.blockingFirst().ready());
        return gameMembersApiService.updateMember(gameService.getCurrentGameID(), memberID, new UpdateMemberDto(status,color));
    }

    public Observable<Member> updateColour(String memberID, String colour) {
        Observable<Member> member = getMember(memberID);
        return gameMembersApiService.updateMember(gameService.getCurrentGameID(), memberID, new UpdateMemberDto(member.blockingFirst().ready(),colour));
    }

    public Observable<Member> getMember(String memberID){
        return gameMembersApiService.getMember(gameID, memberID);
    }

    public void updateID(){
        this.gameID = gameService.getCurrentGameID();
    }
}