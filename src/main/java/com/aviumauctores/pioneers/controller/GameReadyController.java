package com.aviumauctores.pioneers.controller;

import com.aviumauctores.pioneers.App;
import com.aviumauctores.pioneers.Main;
import com.aviumauctores.pioneers.model.Member;
import com.aviumauctores.pioneers.model.User;
import com.aviumauctores.pioneers.service.GameMemberService;
import com.aviumauctores.pioneers.service.UserService;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;

import javax.inject.Inject;
import javax.inject.Provider;
import java.io.IOException;
import java.util.ResourceBundle;

import static com.aviumauctores.pioneers.Constants.FX_SCHEDULER;

public class GameReadyController extends PlayerListController {


    private final App app;
    private final UserService userService;
    private final GameMemberService gameMemberService;
    private final ResourceBundle bundle;
    private final Provider<LobbyController> lobbyController;

    @FXML public Button startGameButton;

    @FXML public Button gameReadyButton;

    @FXML public Button leaveGameButton;

    @FXML public Label gameNameLabel;

    @FXML public Button sendMessageButton;

    @FXML public AnchorPane chatPane;

    @FXML public Tab allChatTab;

    @FXML public TitledPane playerListPane;

    @FXML public ListView<Parent> playerList;

    private int readyMembers;

    private CompositeDisposable disposables;

    @Inject
    public GameReadyController(App app, UserService userService, GameMemberService gameMemberService,
                               ResourceBundle bundle, Provider<LobbyController> lobbyController){
        this.app = app;
        this.userService = userService;
        this.gameMemberService = gameMemberService;
        this.bundle = bundle;
        this.lobbyController = lobbyController;
    }

    public void init(){
        disposables = new CompositeDisposable();
        // Get member list via REST
        disposables.add(gameMemberService.listCurrentGameMembers()
                .subscribe(members -> {
                    for (Member member : members) {
                        String userId = member.userId();
                        disposables.add(userService.getUserByID(userId)
                                .observeOn(FX_SCHEDULER)
                                .subscribe(user -> {
                                    addPlayerToList(user, member);
                                    if (playerListPane != null) {
                                        updatePlayerLabel();
                                    }
                                }));
                    }
                }));
    }

    @Override
    protected void addPlayerToList(User user, Member gameMember) {
        super.addPlayerToList(user, gameMember);
        if (gameMember.ready()) {
            readyMembers++;
        }
    }

    @Override
    protected void updatePlayerLabel() {
        playerListPane.setText(String.format("Spieler im Spiel (Bereit %d/4)", readyMembers));
    }

    public void destroy(){
        if (disposables != null) {
            disposables.dispose();
            disposables = null;
        }
        playerListItemControllers.forEach((id, controller) -> controller.destroy());
        playerListItemControllers.clear();
    }

    public Parent render(){
        final FXMLLoader loader = new FXMLLoader(Main.class.getResource("views/gameReadyScreen.fxml"), bundle);
        loader.setControllerFactory(c -> this);
        final Parent parent;
        try {
            parent = loader.load();
        }catch(IOException e) {
            e.printStackTrace();
            return null;
        }
        playerList.setItems(playerItems);
        updatePlayerLabel();
        return parent;
    }

    public void startGame(ActionEvent actionEvent) {


    }

    public void gameReady(ActionEvent actionEvent) {
    }

    public void leaveGame(ActionEvent actionEvent) {
        final LobbyController controller = lobbyController.get();
        app.show(controller);
    }

    public void sendMessage(ActionEvent actionEvent) {

    }
}
