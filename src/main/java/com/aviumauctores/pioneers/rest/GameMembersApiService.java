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
    Call<List<Member>> listMembers(@Header(HEADER_AUTH) String authToken, @Path(PATH_GAME_ID) String gameId);

    @POST(CREATE_MEMBER_URL)
    Call<Member> createMember(
            @Header(HEADER_AUTH) String authToken,
            @Path(PATH_GAME_ID) String gameId,
            @Body CreateMemberDto createMemberDto
    );

    @GET(GET_MEMBER_URL)
    Call<Member> getMember(
            @Header(HEADER_AUTH) String authToken,
            @Path(PATH_GAME_ID) String gameId, @Path(PATH_USER_ID) String userId
    );

    @PATCH(UPDATE_MEMBER_URL)
    Call<Member> updateMember(
            @Header(HEADER_AUTH) String authToken,
            @Path(PATH_GAME_ID) String gameId, @Path(PATH_USER_ID) String userId,
            @Body UpdateMemberDto updateMemberDto
    );

    @DELETE()
    Call<Member> deleteMember(
            @Header(HEADER_AUTH) String authToken,
            @Path(PATH_GAME_ID) String gameId, @Path(PATH_USER_ID) String userId
    );
}
