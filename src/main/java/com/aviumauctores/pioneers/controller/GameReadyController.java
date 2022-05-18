package com.aviumauctores.pioneers.controller;

import com.aviumauctores.pioneers.App;
import com.aviumauctores.pioneers.Main;
import com.aviumauctores.pioneers.model.Game;
import com.aviumauctores.pioneers.model.Member;
import com.aviumauctores.pioneers.model.User;
import com.aviumauctores.pioneers.service.ErrorService;
import com.aviumauctores.pioneers.service.GameMemberService;
import com.aviumauctores.pioneers.service.GameService;
import com.aviumauctores.pioneers.service.GroupService;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;

import javax.inject.Provider;
import javafx.scene.layout.AnchorPane;

import javax.inject.Inject;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Optional;

import static com.aviumauctores.pioneers.Constants.FX_SCHEDULER;

public class GameReadyController implements Controller {


    private final App app;
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

    private User user;

    @Inject
    public GameReadyController(App app, Provider<LobbyController> lobbyController,
                               GameService gameService, ErrorService errorService, GroupService groupService,
                               GameMemberService gameMemberService){

        this.app = app;
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


    }

    public void destroy(){
        disposable.dispose();

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
