package com.aviumauctores.pioneers.controller;

import com.aviumauctores.pioneers.App;
import com.aviumauctores.pioneers.Main;
import com.aviumauctores.pioneers.service.LoginService;
import com.aviumauctores.pioneers.service.UserService;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import javax.inject.Inject;
import javax.inject.Provider;

import java.io.IOException;
import java.util.ResourceBundle;

import static com.aviumauctores.pioneers.Constants.FX_SCHEDULER;

public class AchievementsController extends PlayerListController {

    private final App app;
    private final ResourceBundle bundle;

    @FXML
    public Button friendsButton;

    @FXML
    public Label playerLabel2;

    @FXML
    public VBox aM1;
    @FXML
    public VBox aM2;
    @FXML
    public VBox aM3;
    @FXML
    public VBox aM4;
    @FXML
    public VBox aM5;
    @FXML
    public VBox aM6;
    @FXML
    public VBox aM7;

    @FXML
    public VBox aM11;




    private final Provider<LobbyController> lobbyController;

    private Color hoverColor = Color.rgb(63,189,63);

    private Color defaultColor= Color.rgb(255,255,255);

    @Inject
    public AchievementsController(App app,
                                  Provider<LobbyController> lobbyController,
                                  ResourceBundle bundle,
                                  LoginService loginService,
                                  UserService userService) {
        super(loginService, userService);
        this.lobbyController = lobbyController;
        this.app = app;
        this.bundle = bundle;

    }

    @Override
    public void init() {
        disposables = new CompositeDisposable();
    }

    public Parent render() {
        final FXMLLoader loader = new FXMLLoader(Main.class.getResource("views/achievementScreen.fxml"), bundle);
        loader.setControllerFactory(c -> this);
        final Parent parent;
        try {
            parent = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        friendsButton.setOnAction(this::onFriends);

        disposables.add(userService.getUserName(userService.getCurrentUserID())
                .observeOn(FX_SCHEDULER)
                .subscribe(name -> playerLabel2.setText(name + "")));

        return parent;
    }

    @Override
    public void destroy(boolean closed) {
        if (closed) {
            disposables.add(userService.changeCurrentUserStatus("offline")
                    .subscribe(() -> {
                        if (disposables != null) {
                            disposables.dispose();
                            disposables = null;
                        }
                    }));
        } else {
            if (disposables != null) {
                disposables.dispose();
                disposables = null;
            }
        }
    }

    public void onFriends(ActionEvent actionEvent) {
    }

    public void leaveAchievments(MouseEvent mouseEvent) {
        final LobbyController controller = lobbyController.get();
        app.show(controller);

    }

    @Override
    protected void updatePlayerLabel() {

    }


    public void halloaM1(MouseEvent mouseEvent) {
        setBackground(aM1,hoverColor);
    }
    public void byeaM1(MouseEvent mouseEvent) {
      setBackground(aM1,defaultColor);
    }

    public void halloaM2(MouseEvent mouseEvent) {
        setBackground(aM2,hoverColor);
    }
    public void byeaM2(MouseEvent mouseEvent) {
        setBackground(aM2,defaultColor);
    }

    public void halloaM3(MouseEvent mouseEvent) {
        setBackground(aM3,hoverColor);
    }
    public void byeaM3(MouseEvent mouseEvent) {
        setBackground(aM3,defaultColor);
    }

    public void halloaM4(MouseEvent mouseEvent) {
        setBackground(aM4,hoverColor);
    }
    public void byeaM4(MouseEvent mouseEvent) {
        setBackground(aM4,defaultColor);
    }

    public void halloaM5(MouseEvent mouseEvent) {
        setBackground(aM5,hoverColor);
    }
    public void byeaM5(MouseEvent mouseEvent) {
        setBackground(aM5,defaultColor);
    }

    public void halloaM6(MouseEvent mouseEvent) {
        setBackground(aM6,hoverColor);
    }

    public void byeaM6(MouseEvent mouseEvent) {
        setBackground(aM6,defaultColor);
    }

    public void halloaM7(MouseEvent mouseEvent) {
        setBackground(aM7,hoverColor);
    }
    public void byeaM7(MouseEvent mouseEvent) {
        setBackground(aM7,defaultColor);
    }

    public void halloaM11(MouseEvent mouseEvent) {
        setBackground(aM11,hoverColor);
    }
    public void byeaM11(MouseEvent mouseEvent) {
        setBackground(aM11,defaultColor);
    }



    public void setBackground(VBox vbox,Color color){
        BackgroundFill bf = new BackgroundFill(color, new CornerRadii(1), null);
        vbox.setBackground(new Background(bf));
    }
}
