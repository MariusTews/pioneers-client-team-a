package com.aviumauctores.pioneers.controller;

import com.aviumauctores.pioneers.App;
import com.aviumauctores.pioneers.Main;
import com.aviumauctores.pioneers.dto.events.EventDto;
import com.aviumauctores.pioneers.model.Member;
import com.aviumauctores.pioneers.model.User;
import com.aviumauctores.pioneers.service.GameMemberService;
import com.aviumauctores.pioneers.model.Game;
import com.aviumauctores.pioneers.model.Member;
import com.aviumauctores.pioneers.model.User;
import com.aviumauctores.pioneers.service.ErrorService;
import com.aviumauctores.pioneers.service.GameMemberService;
import com.aviumauctores.pioneers.service.GameService;
import com.aviumauctores.pioneers.service.UserService;
import com.aviumauctores.pioneers.ws.EventListener;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import com.aviumauctores.pioneers.service.GroupService;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.*;

import javax.inject.Provider;
import javafx.scene.layout.AnchorPane;

import javax.inject.Inject;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Optional;

import static com.aviumauctores.pioneers.Constants.FX_SCHEDULER;

import static com.aviumauctores.pioneers.Constants.FX_SCHEDULER;

public class GameReadyController extends PlayerListController {


    private final App app;
    private final UserService userService;
    private final GameService gameService;
    private final GameMemberService gameMemberService;
    private final EventListener eventListener;
    private final ResourceBundle bundle;
    private final Provider<LobbyController> lobbyController;

    private final GameService gameService;
    private final GameMemberService gameMemberService;
    private final ErrorService errorService;
    public String gameName;

    private Observable<Game> game;

    public final GroupService groupService;

    private CompositeDisposable disposable = new CompositeDisposable();


    public TextField field;
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
    public GameReadyController(App app, UserService userService, GameService gameService, GameMemberService gameMemberService,
                               EventListener eventListener,
                               ResourceBundle bundle, Provider<LobbyController> lobbyController){
        this.app = app;
        this.userService = userService;
        this.gameService = gameService;
        this.gameMemberService = gameMemberService;
        this.eventListener = eventListener;
        this.bundle = bundle;
        this.lobbyController = lobbyController;
        this.gameService = gameService;
        this.errorService = errorService;
        this.groupService = groupService;
        this.game = gameService.getCurrentGame();
        this.gameMemberService = gameMemberService;
        System.out.println("Game id " + gameService.getCurrentGameID());
        System.out.println("owner id " + game.blockingFirst().owner());


    }

    public void init(){
        disposables = new CompositeDisposable();
        // Get member list via REST
        disposables.add(gameMemberService.listCurrentGameMembers()
                        .observeOn(FX_SCHEDULER)
                        .subscribe(members -> {
                            for (Member member : members) {
                                addMemberToList(member);
                            }
                            if (playerListPane != null) {
                                updatePlayerLabel();
                            }
                        }));
        // Listen to game member events
        disposables.add(eventListener.listen(
                        "games." + gameService.getCurrentGameID() + ".members.*.*",
                        Member.class
                )
                .observeOn(FX_SCHEDULER)
                .subscribe(this::onMemberEvent));
    }

    protected void onMemberEvent(EventDto<Member> eventDto) {
        String event = eventDto.event();
        Member member = eventDto.data();
        if (event.endsWith("created")) {
            addMemberToList(member);
        } else if (event.endsWith("updated")) {
            PlayerListItemController controller = playerListItemControllers.get(member.userId());
            if (controller != null) {
                readyMembers += controller.onGameMemberUpdated(member);
            } else {
                addMemberToList(member);
            }
        } else if (event.endsWith("deleted")) {
            PlayerListItemController controller = playerListItemControllers.get(member.userId());
            if (controller != null) {
                removePlayerFromList(member.userId(), controller);
            }
        }
        updatePlayerLabel();
    }

    @Override
    protected void addMemberToList(Member gameMember) {
        String userId = gameMember.userId();
        disposables.add(userService.getUserByID(userId)
                .observeOn(FX_SCHEDULER)
                .subscribe(user -> {
                    addPlayerToList(user, gameMember);
                    // Listen to user events
                    disposables.add(eventListener.listen("users." + userId + ".*", User.class)
                            .observeOn(FX_SCHEDULER)
                            .subscribe(this::onUserEvent));
                }));
        if (gameMember.ready()) {
            readyMembers++;
        }
    }

    @Override
    protected void updatePlayerLabel() {
        playerListPane.setText(String.format(bundle.getString("player.in.game"), readyMembers));
    }

    @Override
    protected void removePlayerFromList(String userID, PlayerListItemController controller) {
        super.removePlayerFromList(userID, controller);
        readyMembers--;
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
        final FXMLLoader loader = new FXMLLoader(Main.class.getResource("views/gameReadyScreen.fxml"));
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
        gameNameLabel.setText("Welcome to " + gameName);
        return parent;
    }


    public void startGame(ActionEvent actionEvent) {
        //TODO: check if the Game has 4 members
        //TODO: check if all players are ready
        //TODO: start the game


    }

    public void gameReady(ActionEvent actionEvent) {


            Observable<Member> member = gameMemberService.getMember(this.user._id());
            gameMemberService.updateMember(this.user._id())
                    .observeOn(FX_SCHEDULER)
                    .subscribe();
            String buttonText = member.blockingFirst().ready() ? "Ready" : "Not Ready";
            gameReadyButton.setText(buttonText);

            System.out.println("Number of members : " + gameService.getCurrentGame().blockingFirst().members());
            System.out.println("Ready status of current User : " + member.blockingFirst().ready());


    }

    public void leaveGame(ActionEvent actionEvent) {
        if(this.user._id().equals(this.game.blockingFirst().owner())){
            ButtonType proceedButton = new ButtonType("Proceed", ButtonBar.ButtonData.OK_DONE);
            ButtonType cancelButton = new ButtonType("Return", ButtonBar.ButtonData.CANCEL_CLOSE);
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Leaver Warning");
            alert.setHeaderText("If you leave a game as the owner, it will be deleted");
            alert.setContentText("Delete this Game?");
            Optional<ButtonType> result = alert.showAndWait();
            if(result.get() == null){
                return;
            }
            if(result.get() == ButtonType.OK){
                gameService.deleteGame()
                        .observeOn(FX_SCHEDULER)
                        .subscribe();
            }else{
                alert.close();
                return;
            }


        }
        else{
            gameMemberService.deleteMember(this.user._id())
                    .observeOn(FX_SCHEDULER)
                    .subscribe();
        }
        gameService.setCurrentGameID(null);
        final LobbyController controller = lobbyController.get();
        controller.setUser(this.user);
        app.show(controller);

    }

    public void sendMessage(ActionEvent actionEvent) {

    }

    public void setUser(User user){
        this.user = user;
    }


}
