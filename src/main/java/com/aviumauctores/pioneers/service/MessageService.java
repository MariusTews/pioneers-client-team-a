package com.aviumauctores.pioneers.service;

import com.aviumauctores.pioneers.dto.groups.CreateGroupDto;
import com.aviumauctores.pioneers.dto.messages.CreateMessageDto;
import com.aviumauctores.pioneers.model.Group;
import com.aviumauctores.pioneers.model.Message;
import com.aviumauctores.pioneers.rest.GroupsApiService;
import com.aviumauctores.pioneers.rest.MessagesApiService;
import io.reactivex.rxjava3.core.Observable;


import javax.inject.Inject;

import static com.aviumauctores.pioneers.Constants.ALLCHAT_ID;
import static com.aviumauctores.pioneers.Constants.SEND_MESSAGE_GAME_NAMESPACE;


public class MessageService {
    private final MessagesApiService messagesApiService;

    @Inject
    public MessageService(MessagesApiService messagesApiService) {

        this.messagesApiService = messagesApiService;
    }

    //
    public Observable<String> sendAllChat(String message) {

        return messagesApiService.sendMessage("groups", ALLCHAT_ID, new CreateMessageDto(message))
                .map(Message::body);
    }



    public Observable<Message> deleteMessage(String id) {

        return messagesApiService.deleteMessage("groups", ALLCHAT_ID, id);
    }

    public Observable<Message> sendMessage(String message, String gameId) {
        return messagesApiService.sendMessage(SEND_MESSAGE_GAME_NAMESPACE, gameId, new CreateMessageDto(message));
    }
}
