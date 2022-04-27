package com.aviumauctores.pioneers.rest;

import com.aviumauctores.pioneers.dto.gamemembers.CreateMemberDto;
import com.aviumauctores.pioneers.dto.gamemembers.UpdateMemberDto;
import com.aviumauctores.pioneers.model.Member;
import retrofit2.Call;
import retrofit2.http.*;

import java.util.List;

public interface GameMembersApiService {
    @GET("games/{gameId}/members")
    Call<List<Member>> listMembers(@Header("Authorization") String authToken, @Path("gameId") String gameId);

    @POST("games/{gameId}/members")
    Call<Member> createMember(
            @Header("Authorization") String authToken,
            @Path("gameId") String gameId,
            @Body CreateMemberDto createMemberDto
    );

    @GET("games/{gameId}/members/{userId}")
    Call<Member> getMember(
            @Header("Authorization") String authToken,
            @Path("gameId") String gameId, @Path("userId") String userId
    );

    @PATCH("games/{gameId}/members/{userId}")
    Call<Member> updateMember(
            @Header("Authorization") String authToken,
            @Path("gameId") String gameId, @Path("userId") String userId,
            @Body UpdateMemberDto updateMemberDto
    );

    @DELETE("games/{gameId}/members/{userId}")
    Call<Member> deleteMember(
            @Header("Authorization") String authToken,
            @Path("gameId") String gameId, @Path("userId") String userId
    );
}
