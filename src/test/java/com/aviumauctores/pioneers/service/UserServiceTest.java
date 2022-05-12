package com.aviumauctores.pioneers.service;

import com.aviumauctores.pioneers.model.Group;
import com.aviumauctores.pioneers.model.User;
import com.aviumauctores.pioneers.rest.GroupsApiService;
import com.aviumauctores.pioneers.rest.UsersApiService;
import io.reactivex.rxjava3.core.Observable;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    UsersApiService usersApiService;

    @InjectMocks
    UserService userService;

    @Test
    void findAll() {
        List<User> users = new ArrayList<>();
        User user1 = new User("1", "Mark", "online", "brr");
        User user2 = new User("2", "Marcel", "online", "brr");
        users.add(user1);
        users.add(user2);
        when(usersApiService.findAll()).thenReturn(Observable.just(users));

        // check if the users will be listed correctly
        List<User> checkUsers = userService.findAll().blockingFirst();
        assertEquals(checkUsers, users);

        verify(usersApiService).findAll();
    }

}