package com.aviumauctores.pioneers.rest;

import com.aviumauctores.pioneers.dto.messages.CreateMessageDto;
import com.aviumauctores.pioneers.dto.messages.UpdateMessageDto;
import com.aviumauctores.pioneers.model.Message;
import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.*;

import java.util.List;

import static com.aviumauctores.pioneers.Constants.*;

public interface MessagesApiService {
    @GET(LIST_MESSAGES_URL)
    Observable<List<Message>> listMessages(
            @Path(PATH_NAMESPACE) String namespace, @Path(PATH_PARENT) String parent,
            @Query(QUERY_CREATED_BEFORE) String createdBefore, @Query(QUERY_LIMIT) int limit
    );

    @POST(SEND_MESSAGE_URL)
    Observable<Message> sendMessage(
            @Path(PATH_NAMESPACE) String namespace, @Path(PATH_PARENT) String parent,
            @Body CreateMessageDto createMessageDto
    );

    @GET(GET_MESSAGE_URL)
    Observable<Message> getMessage(
            @Path(PATH_NAMESPACE) String namespace, @Path(PATH_PARENT) String parent, @Path(PATH_ID) String id
    );

    @PATCH(UPDATE_MESSAGE_URL)
    Observable<Message> updateMessage(
            @Path(PATH_NAMESPACE) String namespace, @Path(PATH_PARENT) String parent, @Path(PATH_ID) String id,
            @Body UpdateMessageDto updateMessageDto
    );

    @DELETE(DELETE_MESSAGE_URL)
    Observable<Message> deleteMessage(
            @Path(PATH_NAMESPACE) String namespace, @Path(PATH_PARENT) String parent, @Path(PATH_ID) String id
    );
}
