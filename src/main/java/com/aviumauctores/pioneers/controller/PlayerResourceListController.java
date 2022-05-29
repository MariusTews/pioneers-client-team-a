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
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
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
    public ScrollPane playerListScrollPane;
    @FXML
    public VBox playerListVBOX;

    private CompositeDisposable disposables;

    private final HashMap<String, String> errorCodes = new HashMap<>();

    private ObservableList<Node> listElements;

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
        disposables = new CompositeDisposable();
        this.listElements = playerListVBOX.getChildren();
        //load players into the list

        disposables.add(eventListener.listen("games." + gameService.getCurrentGameID() + ".members.*.deleted", Member.class )
                .observeOn(FX_SCHEDULER)
                .subscribe(event ->{
                    String leaverID = event.event().split("\\.")[3];
                    for(Node n : listElements){
                        if(n.getId().equals(leaverID)){
                            n.setOpacity(0.3);
                        }
                    }
                }));



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
        playerListVBOX.setPadding(new Insets(10,0,2,20));
        playerListVBOX.setSpacing(10.0);
        for(Member m : gameMemberService.listCurrentGameMembers().blockingFirst()) {
            createPlayerBox(m);
        }
        return parent;
    }

    public void destroy(boolean closed){
        super.destroy(closed);

    }

    public void createPlayerBox(Member member){
        String memberID = member.userId();
        String userName = userService.getUserName(memberID).blockingFirst();
        //String color = colorService.getColor(member.color());
        HBox playerBox = new HBox();
        playerBox.setId(memberID);

        Image image = new Image(Objects.requireNonNull(Main.class.getResource("playerIcons/playerIcon_blue.png")).toString());
        ImageView view = new ImageView(image);
        view.setFitHeight(40.0);
        view.setFitWidth(40.0);

        VBox textBox = new VBox();

        Text playerName = new Text(userName);
        playerName.setFont(new Font(20));
        playerName.setStyle("-fx-font-weight: bold");
        Text resources = new Text("0 Resources");
        textBox.getChildren().addAll(playerName, resources);


        playerBox.getChildren().addAll(view, textBox);
        playerBox.setSpacing(10.0);


        ((VBox)playerListScrollPane.getContent()).getChildren().add(playerBox);
    }


    public void onCurrentPlayerChanged(){

    }

}