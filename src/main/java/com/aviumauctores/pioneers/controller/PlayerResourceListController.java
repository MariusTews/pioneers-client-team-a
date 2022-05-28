package com.aviumauctores.pioneers.controller;

import com.aviumauctores.pioneers.App;
import com.aviumauctores.pioneers.Main;
import com.aviumauctores.pioneers.dto.error.ErrorResponse;
import com.aviumauctores.pioneers.dto.events.EventDto;
import com.aviumauctores.pioneers.model.Game;
import com.aviumauctores.pioneers.model.Member;
import com.aviumauctores.pioneers.model.Message;
import com.aviumauctores.pioneers.model.User;
import com.aviumauctores.pioneers.service.*;
import com.aviumauctores.pioneers.ws.EventListener;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import retrofit2.HttpException;

import javax.inject.Inject;
import javax.inject.Provider;
import java.io.IOException;
import java.util.*;

import static com.aviumauctores.pioneers.Constants.FX_SCHEDULER;

public class PlayerResourceListController extends LoggedInController {


    private final App app;
    private final GameService gameService;
    private final GameMemberService gameMemberService;
    private final ErrorService errorService;
    private final MessageService messageService;

    private final EventListener eventListener;
    private final ResourceBundle bundle;
    private final Provider<LobbyController> lobbyController;

    @FXML
    VBox listVBox;


    private int readyMembers;

    //list for storing message IDs of own messages to check whether a message can be deleted or not
    private final ArrayList<String> ownMessageIds = new ArrayList<>();

    private CompositeDisposable disposables;

    private final HashMap<String, String> errorCodes = new HashMap<>();

    @Inject
    public PlayerResourceListController(App app, UserService userService, GameService gameService, GameMemberService gameMemberService,
                               EventListener eventListener, ErrorService errorService,
                               ResourceBundle bundle, MessageService messageService, Provider<LobbyController> lobbyController)
    {
        super(userService);
        this.app = app;
        this.gameService = gameService;
        this.gameMemberService = gameMemberService;
        this.errorService = errorService;
        this.eventListener = eventListener;
        this.bundle = bundle;
        this.messageService = messageService;
        this.lobbyController = lobbyController;
        gameMemberService.updateID();

    }

    public void init(){

    }


    public Parent render() {
        final FXMLLoader loader = new FXMLLoader(Main.class.getResource("views/playerResourceList.fxml"), bundle);
        loader.setControllerFactory(c -> this);
        final Parent parent;
        try {
            parent = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return parent;
    }

    public void destroy(boolean closed){
        super.destroy(closed);

    }

}