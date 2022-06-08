package com.aviumauctores.pioneers.service;

import com.aviumauctores.pioneers.dto.messages.CreateMessageDto;
import com.aviumauctores.pioneers.model.Message;
import com.aviumauctores.pioneers.rest.MessagesApiService;
import io.reactivex.rxjava3.core.Observable;


import javax.inject.Inject;

import java.util.List;

import static com.aviumauctores.pioneers.Constants.SEND_MESSAGE_GAME_NAMESPACE;


public class MessageService {
    private final MessagesApiService messagesApiService;

    @Inject
    public MessageService(MessagesApiService messagesApiService) {

        this.messagesApiService = messagesApiService;
    }


    public Observable<String> sendGroupMessage(String namespace, String message, String groupId) {

        return messagesApiService.sendMessage(namespace, groupId, new CreateMessageDto(message))
                .map(Message::body);
    }



    public Observable<Message> deleteMessage(String namespace, String id, String groupId) {

        return messagesApiService.deleteMessage(namespace, groupId, id);
    }

    public Observable<Message> deleteGameMessage(String messageId, String gameId) {

        return messagesApiService.deleteMessage("games", gameId, messageId);
    }

    public Observable<List<Message>> listMessages(String namespace, String parent, String createdBefore, int limit) {
        return messagesApiService.listMessages(namespace, parent, createdBefore, limit);
    }

    public Observable<Message> sendGameMessage(String message, String gameId) {
        return messagesApiService.sendMessage(SEND_MESSAGE_GAME_NAMESPACE, gameId, new CreateMessageDto(message));
    }

    public Observable<Message> getMessage(String namespace, String groupId,String id) {
        return messagesApiService.getMessage(namespace, groupId, id);
    }
}