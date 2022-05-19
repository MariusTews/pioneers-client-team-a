package com.aviumauctores.pioneers;

import com.aviumauctores.pioneers.dto.auth.LoginDto;
import com.aviumauctores.pioneers.dto.auth.LoginResult;
import com.aviumauctores.pioneers.dto.auth.RefreshDto;
import com.aviumauctores.pioneers.dto.error.ErrorResponse;
import com.aviumauctores.pioneers.dto.events.EventDto;
import com.aviumauctores.pioneers.dto.gamemembers.CreateMemberDto;
import com.aviumauctores.pioneers.dto.gamemembers.UpdateMemberDto;
import com.aviumauctores.pioneers.dto.games.CreateGameDto;
import com.aviumauctores.pioneers.dto.games.UpdateGameDto;
import com.aviumauctores.pioneers.dto.groups.CreateGroupDto;
import com.aviumauctores.pioneers.dto.groups.UpdateGroupDto;
import com.aviumauctores.pioneers.dto.messages.CreateMessageDto;
import com.aviumauctores.pioneers.dto.messages.UpdateMessageDto;
import com.aviumauctores.pioneers.dto.users.CreateUserDto;
import com.aviumauctores.pioneers.dto.users.UpdateUserDto;
import com.aviumauctores.pioneers.model.*;
import com.aviumauctores.pioneers.rest.*;
import com.aviumauctores.pioneers.service.ErrorService;
import com.aviumauctores.pioneers.service.PreferenceService;
import com.aviumauctores.pioneers.ws.EventListener;
import dagger.Module;
import dagger.Provides;
import io.reactivex.rxjava3.core.Observable;
import okhttp3.MediaType;
import okhttp3.ResponseBody;
import okio.BufferedSource;
import retrofit2.HttpException;

import javax.inject.Singleton;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

@Module
public class TestModule {
    @Provides
    ErrorService errorService() {
        return new ErrorService(null) {
            @Override
            public Object readErrorMessage(HttpException httpException) {
                return new ErrorResponse(501, "", null);
            }
        };
    }

    @Provides
    PreferenceService preferences() {
        return new PreferenceService(null){
            @Override
            public Locale getLocale() {
                return Locale.ROOT;
            }
            @Override
            public void setLocale(Locale locale) {
            }

            @Override
            public String getRefreshToken() {
                return "";
            }

            @Override
            public void setRefreshToken(String token) {
            }

            @Override
            public Boolean getRememberMe() {
                return false;
            }

            @Override
            public void setRememberMe(Boolean rememberMe) {
            }
        };
    }

    @Provides
    EventListener eventListener() {
        return new EventListener(null, null) {
            @Override
            public <T> Observable<EventDto<T>> listen(String pattern, Class<T> payloadType) {
                return Observable.empty();
            }

            @Override
            public void send(Object message) {
                super.send(message);
            }
        };
    }

    @Provides
    @Singleton
    AuthenticationApiService authenticationApiService() {
        return new AuthenticationApiService() {
            @Override
            public Observable<LoginResult> login(LoginDto loginDto) {
                return Observable.just(new LoginResult(
                        "1", "User1", "online", null, "", ""
                ));
            }

            @Override
            public Observable<LoginResult> refresh(RefreshDto refreshDto) {
                return Observable.just(new LoginResult(
                        "1", "User1", "online", null, "", ""
                ));
            }

            @Override
            public Observable<ResponseBody> logout() {
                return Observable.just(new ResponseBody() {
                    @Override
                    public MediaType contentType() {
                        return null;
                    }

                    @Override
                    public long contentLength() {
                        return 0;
                    }

                    @SuppressWarnings("NullableProblems")
                    @Override
                    public BufferedSource source() {
                        return null;
                    }
                });
            }
        };
    }

    @Provides
    @Singleton
    UsersApiService usersApiService() {
        return new UsersApiService() {
            @Override
            public Observable<List<User>> listUsers(String status, String ids) {
                if (Objects.equals(status, "offline")
                        || (ids != null && Arrays.asList(ids.replace(" ", "").split(",")).contains("1"))) {
                    return Observable.just(List.of());
                }
                return Observable.just(
                        List.of(new User("1", "User1", "online", null))
                );
            }

            @Override
            public Observable<User> createUser(CreateUserDto createUserDto) {
                return Observable.just(new User("1", "User1", "online", null));
            }

            @Override
            public Observable<User> getUser(String id) {
                return Observable.just(new User("1", "User1", "online", null));
            }

            @Override
            public Observable<User> updateUser(String id, UpdateUserDto updateUserDto) {
                String name = updateUserDto.name() != null ? updateUserDto.name() : "User" + id;
                String status = updateUserDto.status() != null ? updateUserDto.status() : "online";
                return Observable.just(new User(id, name, status, updateUserDto.avatar()));
            }

            @Override
            public Observable<User> deleteUser(String id) {
                return Observable.just(new User(id, "User" + id, "online", null));
            }

            @Override
            public Observable<List<User>> findAll() {
                return listUsers(null, null);
            }
        };
    }

    @Provides
    @Singleton
    GroupsApiService groupsApiService() {
        return new GroupsApiService() {
            @Override
            public Observable<List<Group>> listGroups(String members) {
                return Observable.just(List.of());
            }

            @Override
            public Observable<Group> createGroup(CreateGroupDto createGroupDto) {
                return Observable.empty();
            }

            @Override
            public Observable<Group> getGroup(String id) {
                return Observable.empty();
            }

            @Override
            public Observable<Group> updateGroup(String id, UpdateGroupDto updateGroupDto) {
                return Observable.empty();
            }

            @Override
            public Observable<Group> deleteGroup(String id) {
                return Observable.empty();
            }
        };
    }

    @Provides
    @Singleton
    MessagesApiService messagesApiService() {
        return new MessagesApiService() {
            @Override
            public Observable<List<Message>> listMessages(String namespace, String parent, String createdBefore, int limit) {
                return Observable.just(List.of());
            }

            @Override
            public Observable<Message> sendMessage(String namespace, String parent, CreateMessageDto createMessageDto) {
                return Observable.just(new Message(
                        "", "", "1001", "1", createMessageDto.body()
                ));
            }

            @Override
            public Observable<Message> getMessage(String namespace, String parent, String id) {
                return Observable.just(new Message(
                        "", "", id, "1", "Message" + id
                ));
            }

            @Override
            public Observable<Message> updateMessage(String namespace, String parent, String id, UpdateMessageDto updateMessageDto) {
                String body = updateMessageDto.body() != null ? updateMessageDto.body() : "Message" + id;
                return Observable.just(new Message(
                        "", "", id, "1", body
                ));
            }

            @Override
            public Observable<Message> deleteMessage(String namespace, String parent, String id) {
                return Observable.just(new Message(
                        "", "", id, "1", "Message" + id
                ));
            }
        };
    }

    @Provides
    @Singleton
    GameMembersApiService gameMembersApiService() {
        return new GameMembersApiService() {
            @Override
            public Observable<List<Member>> listMembers(String gameId) {
                return Observable.empty();
            }

            @Override
            public Observable<Member> createMember(String gameId, CreateMemberDto createMemberDto) {
                return Observable.just(new Member(
                        "", "", gameId, "1", createMemberDto.ready()
                ));
            }

            @Override
            public Observable<Member> getMember(String gameId, String userId) {
                return Observable.just(new Member(
                        "", "", gameId, userId, false
                ));
            }

            @Override
            public Observable<Member> updateMember(String gameId, String userId, UpdateMemberDto updateMemberDto) {
                return Observable.just(new Member(
                        "", "", gameId, userId, updateMemberDto.ready()
                ));
            }

            @Override
            public Observable<Member> deleteMember(String gameId, String userId) {
                return Observable.just(new Member(
                        "", "", gameId, userId, false
                ));
            }
        };
    }

    @Provides
    @Singleton
    GamesApiService gamesApiService() {
        return new GamesApiService() {
            @Override
            public Observable<List<Game>> listGames() {
                return Observable.just(List.of(
                        new Game("", "", "101", "Game101", "1", 1)
                ));
            }

            @Override
            public Observable<Game> createGame(CreateGameDto createGameDto) {
                return Observable.just(new Game(
                        "", "", "101", "Game101", "1", 1
                ));
            }

            @Override
            public Observable<Game> getGame(String id) {
                return Observable.just(new Game(
                        "", "", id, "Game" + id, "1", 1
                ));
            }

            @Override
            public Observable<Game> updateGame(String id, UpdateGameDto updateGameDto) {
                String name = updateGameDto.name() != null ? updateGameDto.name() : "Game" + id;
                String owner = updateGameDto.owner() != null ? updateGameDto.owner() : "1";
                return Observable.just(new Game(
                        "", "", id, name, owner, 1
                ));
            }

            @Override
            public Observable<Game> deleteGame(String id) {
                return Observable.empty();
            }
        };
    }
}
