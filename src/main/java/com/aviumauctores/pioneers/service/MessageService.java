package com.aviumauctores.pioneers.service;

import com.aviumauctores.pioneers.dto.messages.CreateMessageDto;
import com.aviumauctores.pioneers.model.Message;
import com.aviumauctores.pioneers.rest.MessagesApiService;
import io.reactivex.rxjava3.core.Observable;

import javax.inject.Inject;



public class MessageService {
    private final MessagesApiService messagesApiService;

    @Inject
    public MessageService(MessagesApiService messagesApiService) {
        this.messagesApiService = messagesApiService;
    }

    //
    public void /*Observable<String>*/ send(String message) {

        /*return messagesApiService.sendMessage("string", message, new CreateMessageDto(message))
                .map(Message::body);*/
    }


    public void deleteMessage() {

    }
}
