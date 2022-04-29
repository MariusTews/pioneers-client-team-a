package com.aviumauctores.pioneers.rest;

import com.aviumauctores.pioneers.dto.gamemembers.CreateMemberDto;
import com.aviumauctores.pioneers.dto.gamemembers.UpdateMemberDto;
import com.aviumauctores.pioneers.model.Member;
import retrofit2.Call;
import retrofit2.http.*;

import java.util.List;

import static com.aviumauctores.pioneers.Constants.*;

public interface GameMembersApiService {
    @GET(LIST_MEMBERS_URL)
    Call<List<Member>> listMembers(@Path(PATH_GAME_ID) String gameId);

    @POST(CREATE_MEMBER_URL)
    Call<Member> createMember(
            @Path(PATH_GAME_ID) String gameId,
            @Body CreateMemberDto createMemberDto
    );

    @GET(GET_MEMBER_URL)
    Call<Member> getMember(
            @Path(PATH_GAME_ID) String gameId, @Path(PATH_USER_ID) String userId
    );

    @PATCH(UPDATE_MEMBER_URL)
    Call<Member> updateMember(
            @Path(PATH_GAME_ID) String gameId, @Path(PATH_USER_ID) String userId,
            @Body UpdateMemberDto updateMemberDto
    );

    @DELETE(DELETE_MEMBER_URL)
    Call<Member> deleteMember(
            @Path(PATH_GAME_ID) String gameId, @Path(PATH_USER_ID) String userId
    );
}
