package com.aviumauctores.pioneers;

import io.reactivex.rxjava3.core.Scheduler;
import io.reactivex.rxjava3.schedulers.Schedulers;
import javafx.application.Platform;

public class Constants {

    public static final int SCREEN_WIDTH = 640;
    public static final int SCREEN_HEIGHT = 480;

    public static final String GAME_TITLE = "Pioneers";

    public static final String BASE_URL = "https://pioneers.uniks.de/api/v3/";

    // URL params
    public static final String HEADER_AUTH = "Authorization";
    public static final String HEADER_AUTH_BEARER = "Bearer ";
    public static final String PATH_ID = "id";
    public static final String PATH_GAME_ID = "gameId";
    public static final String PATH_USER_ID = "userId";
    public static final String PATH_NAMESPACE = "namespace";
    public static final String PATH_PARENT = "parent";

    public static final String PATH_MOVEID = "moveId";

    public static final String PATH_BUILDING_ID = "parent";

    public static final String QUERY_IDS = "ids";
    public static final String QUERY_STATUS = "status";
    public static final String QUERY_MEMBERS = "members";

    public static final String QUERY_CREATED_BEFORE = "createdBefore";
    public static final String QUERY_LIMIT = "limit";

    public static final String QUERY_USERID = "userId";

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

    // API Pioneers URL

    public static final String GET_MAP_URL = "games/{gameId}/map";

    public static final String LIST_PLAYERS_URL = "games/{gameId}/players";

    public static final String GET_USERID_URL = "games/{gameId}/players/{userId}";

    public static final String GET_STATE_URL = "games/{gameId}/state";

    public static final String LIST_BUILDINGS_URL = "games/{gameId}/buildings";

    public static final String GET_BUILDINGID_URL = "games/{gameId}/buildings/{buildingId}";

    public static final String CREATE_MOVE_URL = "games/{gameId}/moves";


    public static final String GET_MOVES_URL = "games/{gameId}/moves";

    public static final String GET_MOVEID_URL = "games/{gameId}/moves/{moveId}";


    public static final String WS_EVENTS_URL = "wss://pioneers.uniks.de/ws/v3/events";
    public static final String WS_QUERY_AUTH_TOKEN = "?authToken=";

    public static final String JSON_EVENT = "event";
    public static final String JSON_EVENT_SUBSCRIBE = "subscribe";
    public static final String JSON_EVENT_UNSUBSCRIBE = "unsubscribe";

    public static final String JSON_DATA = "data";

    public static final String ALLCHAT_ID = "62a0bcc71359da00145ce5a8";


    //Colors

    public static final int COLOR_AMOUNT = 11;

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
    public static final String COLOR_WHITE = "white";


    //COLOR RGB CODES

    public static final String COLOR_CODE_BLUE = "#0000ff";
    public static final String COLOR_CODE_RED = "#ff0000";
    public static final String COLOR_CODE_GREEN = "#008000";
    public static final String COLOR_CODE_YELLOW = "#ffff00";
    public static final String COLOR_CODE_ORANGE = "#ffa500";
    public static final String COLOR_CODE_VIOLET = "#ee82ee";
    public static final String COLOR_CODE_CYAN = "#00ffff";
    public static final String COLOR_CODE_LIMEGREEN = "#32cd32";
    public static final String COLOR_CODE_MAGENTA = "#ff00ff";
    public static final String COLOR_CODE_CHOCOLATE = "#d2691e";
    public static final String COLOR_CODE_WHITE = "#ffffff";

    //Buildings
    public static final String BUILDING_TYPE_SETTLEMENT = "settlement";
    public static final String BUILDING_TYPE_ROAD = "road";
    public static final String BUILDING_TYPE_CITY = "city";

    //Actions
    public static final String MOVE_FOUNDING_SETTLEMENT = "founding-settlement-";
    public static final String MOVE_FOUNDING_ROAD = "founding-road-";
    public static final String MOVE_FOUNDING_ROLL = "founding-roll";
    public static final String MOVE_ROLL = "roll";
    public static final String MOVE_BUILD = "build";
    public static final String MOVE_DROP = "drop";

    //Resources

    public static final String RESOURCE_BRICK = "brick";
    public static final String RESOURCE_GRAIN = "grain";
    public static final String RESOURCE_LUMBER = "lumber";
    public static final String RESOURCE_ORE = "ore";
    public static final String RESOURCE_WOOL = "wool";
    public static final String RESOURCE_UNKNOWN = "unknown";

    public static final int DROP_WHEN_OVER = 7;


    public static final Scheduler FX_SCHEDULER = Schedulers.from(Platform::runLater);
}