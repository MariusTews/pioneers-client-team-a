package com.aviumauctores.pioneers;

import com.aviumauctores.pioneers.dto.achievements.CreateAchievementDto;
import com.aviumauctores.pioneers.dto.achievements.UpdateAchievementDto;
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
import com.aviumauctores.pioneers.dto.pioneers.CreateMoveDto;
import com.aviumauctores.pioneers.dto.players.UpdatePlayerDto;
import com.aviumauctores.pioneers.dto.rob.RobDto;
import com.aviumauctores.pioneers.dto.users.CreateUserDto;
import com.aviumauctores.pioneers.dto.users.UpdateUserDto;
import com.aviumauctores.pioneers.model.Map;
import com.aviumauctores.pioneers.model.*;
import com.aviumauctores.pioneers.rest.*;
import com.aviumauctores.pioneers.service.ErrorService;
import com.aviumauctores.pioneers.service.PreferenceService;
import com.aviumauctores.pioneers.service.SoundService;
import com.aviumauctores.pioneers.service.TokenStorage;
import com.aviumauctores.pioneers.sounds.GameMusic;
import com.aviumauctores.pioneers.sounds.GameSounds;
import com.aviumauctores.pioneers.ws.EventListener;
import com.fasterxml.jackson.databind.ObjectMapper;
import dagger.Module;
import dagger.Provides;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableEmitter;
import javafx.scene.paint.Color;
import okhttp3.MediaType;
import okhttp3.ResponseBody;
import okio.BufferedSource;
import retrofit2.HttpException;

import javax.inject.Singleton;
import java.net.URL;
import java.util.*;

@Module
public class TestModule {
    @Provides
    ErrorService errorService() {
        ResourceBundle bundle = ResourceBundle.getBundle("com/aviumauctores/pioneers/lang", Locale.ROOT);
        return new ErrorService(null, null, bundle) {
            @Override
            public ErrorResponse readErrorMessage(HttpException httpException) {
                return new ErrorResponse(501, "");
            }

            @Override
            public void handleError(Throwable throwable) {
            }
        };
    }

    @Provides
    SoundService soundService() {
        return new SoundService() {
            @Override
            public GameMusic createGameMusic(URL filePath) {
                return new GameMusic();
            }

            @Override
            public GameSounds createGameSounds(URL filePath) {
                return new GameSounds();
            }
        };
    }

    @Provides
    PreferenceService preferences() {
        return new PreferenceService(null) {
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

    static class TestEventListener extends EventListener {
        java.util.Map<String, ObservableEmitter<?>> emitterMap = new HashMap<>();

        public TestEventListener(TokenStorage tokenStorage, ObjectMapper mapper) {
            super(tokenStorage, mapper);
        }

        @Override
        public <T> Observable<EventDto<T>> listen(String pattern, Class<T> payloadType) {
            return Observable.create(emitter -> {
                emitterMap.put(pattern, emitter);
                emitter.setCancellable(() -> emitterMap.remove(pattern));
            });
        }

        public <T> void fireEvent(String pattern, EventDto<T> eventDto) {
            @SuppressWarnings("unchecked")
            ObservableEmitter<EventDto<T>> emitter = (ObservableEmitter<EventDto<T>>) emitterMap.get(pattern);
            if (emitter != null) {
                emitter.onNext(eventDto);
            }
        }
    }

    @Provides
    @Singleton
    EventListener eventListener() {
        return new TestEventListener(null, null);
    }

    @Provides
    @Singleton
    AuthenticationApiService authenticationApiService() {
        return new AuthenticationApiService() {
            @Override
            public Observable<LoginResult> login(LoginDto loginDto) {
                return Observable.just(new LoginResult(
                        "1", "User1", "online", null, "", "", null
                ));
            }

            @Override
            public Observable<LoginResult> refresh(RefreshDto refreshDto) {
                return Observable.just(new LoginResult(
                        "1", "User1", "online", null, "", "", null
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
                        List.of(new User("1", "User1", "online", null, null))
                );
            }

            @Override
            public Observable<User> createUser(CreateUserDto createUserDto) {
                return Observable.just(new User("1", "User1", "online", null, null));
            }

            @Override
            public Observable<User> getUser(String id) {
                return Observable.just(new User("1", "User1", "online", null, null));
            }

            @Override
            public Observable<User> updateUser(String id, UpdateUserDto updateUserDto) {
                String name = updateUserDto.name() != null ? updateUserDto.name() : "User" + id;
                String status = updateUserDto.status() != null ? updateUserDto.status() : "online";
                return Observable.just(new User(id, name, status, updateUserDto.avatar(), null));
            }

            @Override
            public Observable<User> deleteUser(String id) {
                return Observable.just(new User(id, "User" + id, "online", null, null));
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
                return Observable.just(List.of(
                        new Member("", "", "101", "1", true, Color.GREEN, false)
                ));
            }

            @Override
            public Observable<Member> createMember(String gameId, CreateMemberDto createMemberDto) {
                return Observable.just(new Member(
                        "", "", gameId, "1", createMemberDto.ready(), null, createMemberDto.spectator()
                ));
            }

            @Override
            public Observable<Member> getMember(String gameId, String userId) {
                return Observable.just(new Member(
                        "", "", gameId, userId, false, null, false
                ));
            }

            @Override
            public Observable<Member> updateMember(String gameId, String userId, UpdateMemberDto updateMemberDto) {
                return Observable.just(new Member(
                        "", "", gameId, userId, updateMemberDto.ready(), null, true
                ));
            }

            @Override
            public Observable<Member> deleteMember(String gameId, String userId) {
                return Observable.just(new Member(
                        "", "", gameId, userId, false, null, true
                ));
            }
        };
    }

    @Provides
    @Singleton
    GamesApiService gamesApiService(EventListener eventListener) {
        return new GamesApiService() {
            private final TestEventListener testEventListener = (TestEventListener) eventListener;

            @Override
            public Observable<List<Game>> listGames() {
                return Observable.just(List.of(
                        new Game("", "", "101", "Game101", "1", false, 1, new GameSettings(2, 10))
                ));
            }

            @Override
            public Observable<Game> createGame(CreateGameDto createGameDto) {
                return Observable.just(new Game(
                        "", "", "101", "Game101", "1", false, 1, new GameSettings(2, 10)
                ));
            }

            @Override
            public Observable<Game> getGame(String id) {
                return Observable.just(new Game(
                        "", "", id, "Game" + id, "1", false, 1, new GameSettings(2, 3)
                ));
            }

            @Override
            public Observable<Game> updateGame(String id, UpdateGameDto updateGameDto) {
                String name = updateGameDto.name() != null ? updateGameDto.name() : "Game" + id;
                String owner = updateGameDto.owner() != null ? updateGameDto.owner() : "1";
                Game game = new Game("", "", id, name, owner, updateGameDto.started(), 1, new GameSettings(2, 3));
                testEventListener.fireEvent("games.101.*", new EventDto<>("games.101.updated", game));
                return Observable.just(game);
            }

            @Override
            public Observable<Game> deleteGame(String id) {
                return Observable.empty();
            }
        };
    }

    @Provides
    @Singleton
    PioneersApiService pioneersApiService(EventListener eventListener) {


        return new PioneersApiService() {

            private final TestEventListener testEventListener = (TestEventListener) eventListener;

            @Override
            public Observable<List<Building>> listBuildings(String gameId) {
                return Observable.just(List.of());
            }

            @Override
            public Observable<Building> getBuilding(String gameId, String buildingId) {
                return Observable.empty();
            }

            @Override
            public Observable<Map> getMap(String id) {
                List<Tile> tileList = new ArrayList<>();
                tileList.add(new Tile(0, 0, 0, "desert", 10));

                tileList.add(new Tile(1, -1, 0, "fields", 2));
                tileList.add(new Tile(-1, 0, 1, "hills", 3));
                tileList.add(new Tile(-1, 1, 0, "mountains", 4));
                tileList.add(new Tile(0, 1, -1, "forest", 5));
                tileList.add(new Tile(1, 0, -1, "pasture", 6));
                tileList.add(new Tile(0, -1, 1, "fields", 7));

                tileList.add(new Tile(2, -2, 0, "fields", 8));
                tileList.add(new Tile(1, -2, -1, "hills", 9));
                tileList.add(new Tile(0, -2, 2, "mountains", 10));
                tileList.add(new Tile(-1, -1, 2, "pasture", 11));
                tileList.add(new Tile(-2, 0, 2, "forest", 12));
                tileList.add(new Tile(-2, 1, 1, "pasture", 2));
                tileList.add(new Tile(-2, 2, 0, "hills", 3));
                tileList.add(new Tile(-1, 2, -1, "fields", 4));
                tileList.add(new Tile(0, 2, -2, "mountains", 5));
                tileList.add(new Tile(1, 1, -2, "forest", 6));
                tileList.add(new Tile(2, 0, -2, "pasture", 7));
                tileList.add(new Tile(2, -1, -1, "mountains", 8));


                List<Harbor> harborList = new ArrayList<>();
                harborList.add(new Harbor(2, -2, 0, "lumber", 3));
                harborList.add(new Harbor(0, 2, -2, "brick", 1));
                harborList.add(new Harbor(-2, 0, 2, "ore", 5));

                return Observable.just(new Map("12", tileList, harborList));
            }

            @Override
            public Observable<List<Player>> listMembers(String gameId) {
                List<Player> players = new ArrayList<>();
                players.add(new Player(gameId, "1", "#008000", true, 3, new HashMap<>(), new HashMap<>(), 0, 0));
                return Observable.just(players);
            }

            @Override
            public Observable<Player> getPlayer(String gameId, String userId) {
                return Observable.just(
                        new Player(gameId, userId, "#008000", true, 3, new HashMap<>(), new HashMap<>(), 0, 0)
                );
            }

            @Override
            public Observable<State> getState(String gameId) {
                return Observable.just(
                        new State("", gameId, List.of(
                                new ExpectedMove("founding-settlement-1", List.of("1")),
                                new ExpectedMove("founding-road-1", List.of("1")),
                                new ExpectedMove("founding-settlement-2", List.of("1")),
                                new ExpectedMove("founding-road-2", List.of("1")),
                                new ExpectedMove("roll", List.of("1"))
                        ), new Point3D(1, 1, 1))
                );
            }

            @Override
            public Observable<List<Move>> getMoves(String gameId, String userId) {
                return Observable.just(List.of());
            }

            @Override
            public Observable<Move> getMoveId(String gameId, String userId) {
                return Observable.just(
                        new Move("", "", gameId, userId, "action", 1, "", new RobDto(1, 2, 3, ""), new HashMap<>(), "")
                );
            }

            @Override
            public Observable<Player> updatePlayer(String gameId, String userId, UpdatePlayerDto updatePlayerDto) {
                return Observable.just(new Player(
                        "", "", null, true, 1, new HashMap<>(), new HashMap<>(), 0, 0
                ));
            }


            @Override
            public Observable<Move> createMove(String gameId, CreateMoveDto createMoveDto) {
                if (createMoveDto.building() != null) {
                    Building building = createMoveDto.building();
                    testEventListener.fireEvent("games.101.buildings.*.*", new EventDto<>("games.101.updated", building));
                    State state;
                    Player player;
                    HashMap<String, Integer> resources = new HashMap<>();
                    HashMap<String, Integer> remainingBuildings = new HashMap<>();
                    switch (createMoveDto.action()) {
                        case "founding-settlement-1" -> {
                            state = new State("", gameId, List.of(
                                    new ExpectedMove("founding-road-1", List.of("1")),
                                    new ExpectedMove("founding-settlement-2", List.of("1")),
                                    new ExpectedMove("founding-road-2", List.of("1")),
                                    new ExpectedMove("roll", List.of("1"))
                            ), new Point3D(1, 1, 1));
                            resources.put("lumber", 10);
                            resources.put("ore", 10);
                            resources.put("grain", 10);
                            resources.put("brick", 10);
                            resources.put("wool", 10);
                            remainingBuildings.put("road", 15);
                            remainingBuildings.put("settlement", 4);
                            remainingBuildings.put("city", 4);
                            player = new Player(gameId, "1", null, true, 1, resources, remainingBuildings, 1, 0);
                        }
                        case "founding-road-1" -> {
                            state = new State("", gameId, List.of(
                                    new ExpectedMove("founding-settlement-2", List.of("1")),
                                    new ExpectedMove("founding-road-2", List.of("1")),
                                    new ExpectedMove("roll", List.of("1"))
                            ), new Point3D(1, 1, 1));
                            resources.put("lumber", 10);
                            resources.put("ore", 10);
                            resources.put("grain", 10);
                            resources.put("brick", 10);
                            resources.put("wool", 10);
                            remainingBuildings.put("road", 14);
                            remainingBuildings.put("settlement", 4);
                            remainingBuildings.put("city", 4);
                            player = new Player(gameId, "1", null, true, 1, resources, remainingBuildings, 1, 0);
                        }
                        case "founding-settlement-2" -> {
                            state = new State("", gameId, List.of(
                                    new ExpectedMove("founding-road-2", List.of("1")),
                                    new ExpectedMove("roll", List.of("1"))
                            ), new Point3D(1, 1, 1));
                            resources.put("lumber", 10);
                            resources.put("ore", 10);
                            resources.put("grain", 10);
                            resources.put("brick", 10);
                            resources.put("wool", 10);
                            remainingBuildings.put("road", 14);
                            remainingBuildings.put("settlement", 3);
                            remainingBuildings.put("city", 4);
                            player = new Player(gameId, "1", null, true, 1, resources, remainingBuildings, 2, 0);
                        }
                        case "founding-road-2" -> {
                            state = new State("", gameId, List.of(
                                    new ExpectedMove("roll", List.of("1"))
                            ), new Point3D(1, 1, 1));
                            resources.put("lumber", 10);
                            resources.put("ore", 10);
                            resources.put("grain", 10);
                            resources.put("brick", 10);
                            resources.put("wool", 10);
                            remainingBuildings.put("road", 13);
                            remainingBuildings.put("settlement", 3);
                            remainingBuildings.put("city", 4);
                            player = new Player(gameId, "1", null, true, 1, resources, remainingBuildings, 2, 0);
                        }
                        default -> {
                            state = new State("", gameId, List.of(
                                    new ExpectedMove("build", List.of("1"))
                            ), new Point3D(1, 1, 1));

                            if (createMoveDto.building().type().equals("settlement")) {
                                resources.put("lumber", 10);
                                resources.put("ore", 10);
                                resources.put("grain", 10);
                                resources.put("brick", 10);
                                resources.put("wool", 10);
                                remainingBuildings.put("road", 12);
                                remainingBuildings.put("settlement", 2);
                                remainingBuildings.put("city", 4);
                                player = new Player(gameId, "1", null, true, 1, resources, remainingBuildings, 3, 0);
                            }
                            else {
                                resources.put("lumber", 10);
                                resources.put("ore", 10);
                                resources.put("grain", 10);
                                resources.put("brick", 10);
                                resources.put("wool", 10);
                                remainingBuildings.put("road", 12);
                                remainingBuildings.put("settlement", 3);
                                remainingBuildings.put("city", 4);
                                player = new Player(gameId, "1", null, true, 1, resources, remainingBuildings, 2, 0);
                            }
                        }

                    }
                    testEventListener.fireEvent("games.101.state.*", new EventDto<>("games.101.updated", state));
                    testEventListener.fireEvent("games.101.players.*.updated", new EventDto<>("games.101.updated", player));

                    return Observable.just(new Move("1", "2", gameId, "1", "build", 3, "building", null, null, null));
                } else if (Objects.equals(createMoveDto.action(), "roll")) {
                    State state = new State("", gameId, List.of(
                            new ExpectedMove("build", List.of("1"))
                    ), new Point3D(1, 1, 1));
                    testEventListener.fireEvent("games.101.state.*", new EventDto<>("games.101.updated", state));
                    testEventListener.fireEvent("games.101.moves.*.*", new EventDto<>("games.101.updated", new Move("1", "2", gameId, "1", "roll", 3, null, null, null, null)));
                } else if (Objects.equals(createMoveDto.action(), "build")) {
                    State state = new State("", gameId, List.of(
                            new ExpectedMove("roll", List.of("1"))
                    ), new Point3D(1, 1, 1));
                    testEventListener.fireEvent("games.101.state.*", new EventDto<>("games.101.updated", state));
                }
                return Observable.empty();
            }
        };
    }

    @Provides
    @Singleton
    AchievementsApiService achievementsApiService() {
        return new AchievementsApiService() {
            @Override
            public Observable<List<AchievementSummary>> listAchievements() {
                return Observable.just(List.of());
            }

            @Override
            public Observable<AchievementSummary> getAchievement(String id) {
                return Observable.just(new AchievementSummary("test", 0, 0, 0));
            }

            @Override
            public Observable<List<Achievement>> listUserAchievements(String id) {
                return Observable.just(List.of());
            }

            @Override
            public Observable<List<Achievement>> getUserAchievement(String userId, String id) {
                return Observable.just(List.of());
            }

            @Override
            public Observable<Achievement> putAchievement(String userId, String id, CreateAchievementDto createAchievementDto) {
                return Observable.just(new Achievement("mount doom", "nothing", "12", "42", "the door", 5));
            }

            @Override
            public Observable<Achievement> updateAchievement(String userId, String id, UpdateAchievementDto updateAchievementDto) {
                return Observable.just(new Achievement("mount doom", "nothing", "12", "42", "the door", 5));
            }

            @Override
            public Observable<Achievement> deleteAchievement(String userId, String id) {
                return Observable.just(new Achievement("mount doom", "nothing", "12", "42", "the door", 5));
            }
        };
    }
}
