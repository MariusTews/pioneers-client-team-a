package com.aviumauctores.pioneers.rest;

import com.aviumauctores.pioneers.dto.messages.CreateMessageDto;
import com.aviumauctores.pioneers.dto.messages.UpdateMessageDto;
import com.aviumauctores.pioneers.model.Message;
import retrofit2.Call;
import retrofit2.http.*;

import java.util.List;

public interface MessagesApiService {
    @GET("{namespace}/{parent}/messages")
    Call<List<Message>> listMessages(
            @Header("Authorization:") String authToken,
            @Path("namespace") String namespace, @Path("parent") String parent,
            @Query("createdBefore") String createdBefore, @Query("limit") int limit
    );

    @POST("{namespace}/{parent}/messages")
    Call<Message> sendMessage(
            @Header("Authorization:") String authToken,
            @Path("namespace") String namespace, @Path("parent") String parent,
            @Body CreateMessageDto createMessageDto
    );

    @GET("{namespace}/{parent}/messages/{id}")
    Call<Message> getMessage(
            @Header("Authorization:") String authToken,
            @Path("namespace") String namespace, @Path("parent") String parent, @Path("id") String id
    );

    @PATCH("{namespace}/{parent}/messages/{id}")
    Call<Message> updateMessage(
            @Header("Authorization:") String authToken,
            @Path("namespace") String namespace, @Path("parent") String parent, @Path("id") String id,
            @Body UpdateMessageDto updateMessageDto
    );

    @DELETE("{namespace}/{parent}/messages/{id}")
    Call<Message> deleteMessage(
            @Header("Authorization:") String authToken,
            @Path("namespace") String namespace, @Path("parent") String parent, @Path("id") String id
    );
}
