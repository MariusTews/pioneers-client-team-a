package com.aviumauctores.pioneers;

import io.reactivex.rxjava3.core.Scheduler;
import io.reactivex.rxjava3.schedulers.Schedulers;
import javafx.application.Platform;

public class Constants {

    public static final int SCREEN_WIDTH = 640;
    public static final int SCREEN_HEIGHT = 480;

    public static final String GAME_TITLE = "Pioneers";

    public static final String BASE_URL = "https://pioneers.uniks.de/api/v1/";

    // URL params
    public static final String HEADER_AUTH = "Authorization";
    public static final String HEADER_AUTH_BEARER = "Bearer ";
    public static final String PATH_ID = "id";
    public static final String PATH_GAME_ID = "gameId";
    public static final String PATH_USER_ID = "userId";
    public static final String PATH_NAMESPACE = "namespace";
    public static final String PATH_PARENT = "parent";
    public static final String QUERY_IDS = "ids";
    public static final String QUERY_STATUS = "status";
    public static final String QUERY_MEMBERS = "members";

    public static final String QUERY_CREATED_BEFORE = "createdBefore";
    public static final String QUERY_LIMIT = "limit";

    // Authentication API URLs
    public static final String LOGIN_URL = "auth/login";
    public static final String REFRESH_URL = "auth/refresh";
    public static final String LOGOUT_URL = "auth/logout";

    // Users API URLs
    public static final String LIST_USERS_URL = "users";
    public static final String CREATE_USER_URL = "users";
    public static final String GET_USER_URL = "users/{id}";
    public static final String UPDATE_USER_URL = "users/{id}";
    public static final String DELETE_USER_URL = "users/{id}";

    // Groups API URLs
    public static final String LIST_GROUPS_URL = "groups";
    public static final String CREATE_GROUP_URL = "groups";
    public static final String GET_GROUP_URL = "groups/{id}";
    public static final String UPDATE_GROUP_URL = "groups/{id}";
    public static final String DELETE_GROUP_URL = "groups/{id}";

    // Messages API URLs
    public static final String LIST_MESSAGES_URL = "{namespace}/{parent}/messages";
    public static final String SEND_MESSAGE_URL = "{namespace}/{parent}/messages";
    public static final String GET_MESSAGE_URL = "{namespace}/{parent}/messages/{id}";
    public static final String UPDATE_MESSAGE_URL = "{namespace}/{parent}/messages/{id}";
    public static final String DELETE_MESSAGE_URL = "{namespace}/{parent}/messages/{id}";

    public static final String SEND_MESSAGE_GAME_NAMESPACE = "games";

    // Game Members API URLs
    public static final String LIST_MEMBERS_URL = "games/{gameId}/members";
    public static final String CREATE_MEMBER_URL = "games/{gameId}/members";
    public static final String GET_MEMBER_URL = "games/{gameId}/members/{userId}";
    public static final String UPDATE_MEMBER_URL = "games/{gameId}/members/{userId}";
    public static final String DELETE_MEMBER_URL = "games/{gameId}/members/{userId}";

    // Games API URLs
    public static final String LIST_GAMES_URL = "games";
    public static final String CREATE_GAME_URL = "games";
    public static final String GET_GAME_URL = "games/{id}";
    public static final String UPDATE_GAME_URL = "games/{id}";
    public static final String DELETE_GAME_URL = "games/{id}";

    public static final String WS_EVENTS_URL = "wss://pioneers.uniks.de/ws/v1/events";
    public static final String WS_QUERY_AUTH_TOKEN = "?authToken=";

    public static final String JSON_EVENT = "event";
    public static final String JSON_EVENT_SUBSCRIBE = "subscribe";
    public static final String JSON_EVENT_UNSUBSCRIBE = "unsubscribe";

    public static final String JSON_DATA = "data";

    public static final String ALLCHAT_ID = "627cf3c93496bc00158f3859";


    //Colors

    public static  final int COLOR_AMOUNT = 10;

    public static final String COLOR_BLUE = "blue";
    public static final String COLOR_RED = "red";
    public static final String COLOR_GREEN = "green";
    public static final String COLOR_YELLOW = "yellow";
    public static final String COLOR_ORANGE = "orange";
    public static final String COLOR_VIOLET = "violet";
    public static final String COLOR_CYAN = "cyan";
    public static final String COLOR_LIMEGREEN = "limegreen";
    public static final String COLOR_MAGENTA = "magenta";
    public static final String COLOR_CHOCOLATE = "chocolate";


    //COLOR RGB CODES

    public static final String COLOR_CODE_BLUE = "#0000FF";
    public static final String COLOR_CODE_RED = "#FF0000";
    public static final String COLOR_CODE_GREEN = "#008000";
    public static final String COLOR_CODE_YELLOW = "#FFFF00";
    public static final String COLOR_CODE_ORANGE = "#FFA500";
    public static final String COLOR_CODE_VIOLET = "#EE82EE";
    public static final String COLOR_CODE_CYAN = " #00FFFF";
    public static final String COLOR_CODE_LIMEGREEN = "#32CD32";
    public static final String COLOR_CODE_MAGENTA = "#FF00FF";
    public static final String COLOR_CODE_CHOCOLATE = "#D2691E";












    public static final Scheduler FX_SCHEDULER = Schedulers.from(Platform::runLater);
}