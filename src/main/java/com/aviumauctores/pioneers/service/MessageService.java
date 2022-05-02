package com.aviumauctores.pioneers.service;

import com.aviumauctores.pioneers.dto.messages.CreateMessageDto;
import com.aviumauctores.pioneers.model.Message;
import com.aviumauctores.pioneers.rest.MessagesApiService;
import io.reactivex.rxjava3.core.Observable;

import javax.inject.Inject;
import java.util.function.Consumer;

public class MessageService {
    private final MessagesApiService messageApiService;

    @Inject
    public MessageService(MessagesApiService messagesApiService) {
        this.messageApiService = messagesApiService;
    }


    public void send(String message, Consumer<? super Observable<Message>> callback) {

    }


    public void deleteMessage() {

    }
}
