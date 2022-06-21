package com.aviumauctores.pioneers.service;

import com.aviumauctores.pioneers.dto.users.CreateUserDto;
import com.aviumauctores.pioneers.dto.users.UpdateUserDto;
import com.aviumauctores.pioneers.model.User;
import com.aviumauctores.pioneers.rest.UsersApiService;
import io.reactivex.rxjava3.core.Observable;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
        User user1 = new User("1", "Mark", "online", "brr", null);
        User user2 = new User("2", "Marcel", "online", "brr", null);
        users.add(user1);
        users.add(user2);
        when(usersApiService.findAll()).thenReturn(Observable.just(users));

        // check if the users will be listed correctly
        List<User> checkUsers = userService.findAll().blockingFirst();
        assertEquals(checkUsers, users);

        verify(usersApiService).findAll();
    }


    @Test
    void listOnlineUsers() {
        User user1 = new User("1", "Mark", "online", "brr", null);
        User user2 = new User("2", "Marcel", "online", "brr", null);

        List<User> usersOnline = new ArrayList<>();
        usersOnline.add(user1);
        usersOnline.add(user2);
        when(userService.listOnlineUsers()).thenReturn(Observable.just(usersOnline));

        List<User> checkUsers = userService.listOnlineUsers().blockingFirst();
        assertEquals(checkUsers, usersOnline);

        verify(usersApiService).listUsers("online", null);
    }

    @Test
    void register() {
        User user1 = new User("1", "Mark", "online", "brr", null);
        when(userService.register("Mark", "password")).thenReturn(Observable.just(user1));

        User user2 = userService.register("Mark", "password").blockingFirst();
        assertEquals(user2, user1);
        verify(usersApiService).createUser(new CreateUserDto("Mark", "password", null));
    }

    @Test
    void getUserByID() {
        User user1 = new User("1", "Mark", "online", "brr", null);
        when(userService.getUserByID("1")).thenReturn(Observable.just(user1));

        User user = userService.getUserByID("1").blockingFirst();
        assertEquals(user, user1);
        verify(usersApiService).getUser("1");
    }

    @Test
    void getUserName() {
        User user1 = new User("1", "Mark", "online", "brr", null);
        when(userService.getUserByID("1")).thenReturn((Observable.just(user1)));

        String username = userService.getUserName("1").blockingFirst();
        assertEquals(username, user1.name());
        verify(usersApiService).getUser("1");

    }

    @Test
    void updateUser() {
        User user1 = new User("1", "Mark", "offline", "brr", null);

        when(userService.updateUser("1", new UpdateUserDto("Mark", "offline", "brr", "test", null))).thenReturn(Observable.just(user1));
        User user = userService.updateUser("1", new UpdateUserDto("Mark", "offline", "brr", "test", null)).blockingFirst();

        assertEquals(user, user1);
        verify(usersApiService).updateUser("1", new UpdateUserDto("Mark", "offline", "brr", "test", null));
    }
}