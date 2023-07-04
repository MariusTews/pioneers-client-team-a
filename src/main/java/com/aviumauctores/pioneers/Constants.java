package com.aviumauctores.pioneers;

import io.reactivex.rxjava3.core.Scheduler;
import io.reactivex.rxjava3.schedulers.Schedulers;
import javafx.application.Platform;

import java.util.HashMap;

public class Constants {

    public static final int SCREEN_WIDTH = 640;
    public static final int SCREEN_HEIGHT = 480;

    public static final String GAME_TITLE = "Pioneers";

    public static void setURL(String URL) {
        BASE_URL = "http://" + URL + "/api/v3/";
        WS_EVENTS_URL = "ws://" + URL + "/ws/v3/events";
    }

    public static String BASE_URL = "http://placeholder/";
    public static String WS_EVENTS_URL = "ws://placeholder/";
    public static final String WS_QUERY_AUTH_TOKEN = "?authToken=";

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
    public static final String CHECK_URL = "auth/check";

    // Users API URLs
    public static final String LIST_USERS_URL = "users";
    public static final String CREATE_USER_URL = "users";
    public static final String GET_USER_URL = "users/{id}";
    public static final String UPDATE_USER_URL = "users/{id}";

    // Groups API URLs
    public static final String LIST_GROUPS_URL = "groups";
    public static final String CREATE_GROUP_URL = "groups";
    public static final String UPDATE_GROUP_URL = "groups/{id}";

    // Messages API URLs
    public static final String LIST_MESSAGES_URL = "{namespace}/{parent}/messages";
    public static final String SEND_MESSAGE_URL = "{namespace}/{parent}/messages";
    public static final String GET_MESSAGE_URL = "{namespace}/{parent}/messages/{id}";
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

    // Achievements API Service

    public static final String ACHIEVEMENTS_URL = "achievements";
    public static final String USER_ACHIEVEMENTS_URL = "users/{userId}/" + ACHIEVEMENTS_URL;
    public static final String USER_ACHIEVEMENTS_BY_ID_URL = USER_ACHIEVEMENTS_URL + "/{id}";

    // API Pioneers URL

    public static final String GET_MAP_URL = "games/{gameId}/map";

    public static final String LIST_PLAYERS_URL = "games/{gameId}/players";

    public static final String GET_USERID_URL = "games/{gameId}/players/{userId}";

    public static final String GET_STATE_URL = "games/{gameId}/state";

    public static final String LIST_BUILDINGS_URL = "games/{gameId}/buildings";

    public static final String CREATE_MOVE_URL = "games/{gameId}/moves";

    public static final String JSON_EVENT = "event";
    public static final String JSON_EVENT_SUBSCRIBE = "subscribe";
    public static final String JSON_EVENT_UNSUBSCRIBE = "unsubscribe";

    public static final String JSON_DATA = "data";

    public static final String ALLCHAT_ID = "62a0bcc71359da00145ce5a8";


    //Colors
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
    public static final String MOVE_ROB = "rob";

    //Resources
    public static final String RESOURCE_BRICK = "brick";
    public static final String RESOURCE_GRAIN = "grain";
    public static final String RESOURCE_LUMBER = "lumber";
    public static final String RESOURCE_ORE = "ore";
    public static final String RESOURCE_WOOL = "wool";
    public static final String RESOURCE_UNKNOWN = "unknown";

    //Stats
    public static final String STAT_RESOURCES_GAINED = "resources.gained";
    public static final String STAT_RESOURCES_LOST = "resources.lost";
    public static final String STAT_LONGEST_ROAD = "longest.road";
    public static final String STAT_CITIES_BUILT = "cities.built";
    public static final String STAT_SETTLEMENTS_BUILT = "settlements.built";
    public static final String STAT_ROADS_BUILT = "road.built";
    public static final String[] ALL_STAT_NAMES = new String[]{STAT_RESOURCES_GAINED, STAT_RESOURCES_LOST, STAT_LONGEST_ROAD,
        STAT_CITIES_BUILT, STAT_SETTLEMENTS_BUILT, STAT_ROADS_BUILT};
    public static final Scheduler FX_SCHEDULER = Schedulers.from(Platform::runLater);


    // values for smallest map-radius
    public static final double WIDTH_HEXAGON = 518.75;
    public static final double HEIGHT_HEXAGON = 446.875;
    //public static final double MAIN_PANE_MIDDLE_X = 291.5;
    public static final double MAIN_PANE_MIDDLE_X = 420;
    //public static final double MAIN_PANE_MIDDLE_Y = 290.0;
    public static final double MAIN_PANE_MIDDLE_Y = 350.0;
    public static final double WIDTH_HEIGHT_BUILDING = 90.0;
    public static final double HEIGHT_ROAD = 50.0;
    public static final double WIDTH_ROAD = 179.375;

    // achievement-ids
    public static final String ACHIEVEMENT_SETTLEMENTS = "build-settlements";
    public static final String ACHIEVEMENT_ROADS = "build-road";
    public static final String ACHIEVEMENT_CITIES = "build-CITIES";
    public static final String ACHIEVEMENT_ALL = "all-achievements-completed";
    public static final String ACHIEVEMENT_WIN = "win-game";
    public static final String WINSTREAK = "winstreak";
    public static final String ACHIEVEMENTS_WIN_LONGEST = "win-game-longest-road";
    public static final String ACHIEVEMENT_RESOURCES = "many-resources";
    public static final String ACHIEVEMENT_TRADE = "good-trade-offer";
    public static final String RANKING = "ranking";
    public static final HashMap<String, Integer> ACHIEVEMENT_UNLOCK_VALUES = new HashMap<>();
    static {
        ACHIEVEMENT_UNLOCK_VALUES.put(ACHIEVEMENT_SETTLEMENTS, 100);
        ACHIEVEMENT_UNLOCK_VALUES.put(ACHIEVEMENT_ROADS, 100);
        ACHIEVEMENT_UNLOCK_VALUES.put(ACHIEVEMENT_CITIES, 100);
        ACHIEVEMENT_UNLOCK_VALUES.put(ACHIEVEMENT_ALL, 7);
        ACHIEVEMENT_UNLOCK_VALUES.put(ACHIEVEMENT_WIN, 10);
        ACHIEVEMENT_UNLOCK_VALUES.put(ACHIEVEMENTS_WIN_LONGEST, 3);
        ACHIEVEMENT_UNLOCK_VALUES.put(ACHIEVEMENT_RESOURCES, 25);
        ACHIEVEMENT_UNLOCK_VALUES.put(ACHIEVEMENT_TRADE, 5);
    }
}