package com.aviumauctores.pioneers.service;

import com.aviumauctores.pioneers.dto.messages.CreateMessageDto;
import com.aviumauctores.pioneers.model.Message;
import com.aviumauctores.pioneers.rest.MessagesApiService;
import io.reactivex.rxjava3.core.Observable;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MessageServiceTest {

    @Mock
    MessagesApiService messagesApiService;

    @InjectMocks
    MessageService messageService;

    @Test
    void sendGroupMessage() {
        Message msg = new Message("1", "2", "42", "43", "hello");
        when(messagesApiService.sendMessage(any(), any(), any())).thenReturn(Observable.just(msg));

        // check if the group will be sended correctly
        String checkmsg = messageService.sendGroupMessage("groups", "hello", "627cf3c93496bc00158f3859").blockingFirst();
        assertEquals(msg.body(), checkmsg);

        verify(messagesApiService).sendMessage("groups", "627cf3c93496bc00158f3859", new CreateMessageDto("hello"));
    }

    @Test
    void delete() {
        Message msg = new Message("1", "2", "42", "43", "hello");
        when(messagesApiService.deleteMessage(any(), any(), any())).thenReturn(Observable.just(msg));

        // check if the group will be deleted correctly
        Message dltmsg = messageService.deleteMessage("groups", "42", "627cf3c93496bc00158f3859").blockingFirst();
        assertEquals(dltmsg, msg);

        verify(messagesApiService).deleteMessage("groups", "627cf3c93496bc00158f3859", "42");

    }

    @Test
    void deleteGameMessage() {
        Message msg = new Message("1", "2", "42", "43", "hello");
        when(messagesApiService.deleteMessage(any(), any(), any())).thenReturn(Observable.just(msg));

        // check if the game message will be deleted correctly
        Message dltmsg = messageService.deleteGameMessage("42", "627cf3c93496bc00158f3859").blockingFirst();
        assertEquals(dltmsg, msg);

        verify(messagesApiService).deleteMessage("games", "627cf3c93496bc00158f3859", "42");
    }

    @Test
    void listMessages() {
        Message msg = new Message("1", "2", "42", "43", "hello");
        Message msg1 = new Message("2", "3", "43", "44", "hello back");
        List<Message> msgList = new ArrayList<>();
        msgList.add(msg);
        msgList.add(msg1);
        when(messagesApiService.listMessages("groups", "12", "13", 12)).thenReturn(Observable.just(msgList));

        // check if the messages are listed correctly
        List<Message> back = messageService.listMessages("groups", "12", "13", 12).blockingFirst();
        assertEquals(back, msgList);

        verify(messagesApiService).listMessages("groups", "12", "13", 12);


    }

    @Test
    void sendGameMessage() {
        Message msg = new Message("1", "2", "42", "43", "hello");
        when(messagesApiService.sendMessage(any(), any(), any())).thenReturn(Observable.just(msg));

        // check if the game message will be sended correctly
        Message checkmsg = messageService.sendGameMessage("hello", "627cf3c93496bc00158f3859").blockingFirst();
        assertEquals(msg, checkmsg);

        verify(messagesApiService).sendMessage("games", "627cf3c93496bc00158f3859", new CreateMessageDto("hello"));
    }

    @Test
    void getMessage() {
        Message msg = new Message("1", "2", "42", "43", "hello");
        when(messagesApiService.getMessage(any(), any(), any())).thenReturn(Observable.just(msg));

        // check if the message is correct
        Message checkmsg = messageService.getMessage("groups", "627cf3c93496bc00158f3859", "1").blockingFirst();
        assertEquals(msg, checkmsg);

        verify(messagesApiService).getMessage("groups", "627cf3c93496bc00158f3859", "1");
    }
}