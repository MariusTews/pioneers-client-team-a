package com.aviumauctores.pioneers.service;

import com.aviumauctores.pioneers.dto.groups.CreateGroupDto;
import com.aviumauctores.pioneers.dto.messages.CreateMessageDto;
import com.aviumauctores.pioneers.model.Group;
import com.aviumauctores.pioneers.model.Message;
import com.aviumauctores.pioneers.rest.GroupsApiService;
import com.aviumauctores.pioneers.rest.MessagesApiService;
import io.reactivex.rxjava3.core.Observable;


import javax.inject.Inject;

import java.util.List;

import static com.aviumauctores.pioneers.Constants.ALLCHAT_ID;


public class MessageService {
    private final MessagesApiService messagesApiService;

    @Inject
    public MessageService(MessagesApiService messagesApiService) {

        this.messagesApiService = messagesApiService;
    }


    public Observable<String> sendMessage(String message, String groupId) {

        return messagesApiService.sendMessage("groups", groupId, new CreateMessageDto(message))
                .map(Message::body);
    }



    public Observable<Message> deleteMessage(String id, String groupId) {

        return messagesApiService.deleteMessage("groups", groupId, id);
    }

    public Observable<List<Message>> listMessages(String namespace, String parent, String createdBefore, int limit) {
        return messagesApiService.listMessages(namespace, parent, createdBefore, limit);
    }
}
