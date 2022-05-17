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

import static org.junit.jupiter.api.Assertions.*;
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
    void sendMessage() {
        Message msg = new Message("1", "2", "42", "43", "hello");
        when(messagesApiService.sendMessage(any(), any(), any())).thenReturn(Observable.just(msg));

        // check if the group will be sended correctly
        String checkmsg = messageService.sendMessage("hello", "627cf3c93496bc00158f3859").blockingFirst();
        assertEquals(msg.body(), checkmsg);

        verify(messagesApiService).sendMessage("groups", "627cf3c93496bc00158f3859", new CreateMessageDto("hello"));
    }

   @Test
   void delete() {
       Message msg = new Message("1", "2", "42", "43", "hello");
       when(messagesApiService.deleteMessage(any(), any(), any())).thenReturn(Observable.just(msg));

       // check if the group will be deleted correctly
       Message dltmsg = messageService.deleteMessage("42","627cf3c93496bc00158f3859").blockingFirst();
       assertEquals(dltmsg, msg);

       verify(messagesApiService).deleteMessage("groups", "627cf3c93496bc00158f3859", "42");

   }

}