package com.aviumauctores.pioneers.controller;

import com.aviumauctores.pioneers.App;
import com.aviumauctores.pioneers.Main;
import com.aviumauctores.pioneers.service.LoginService;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;

import javax.inject.Inject;
import javax.inject.Provider;
import java.io.IOException;

import static com.aviumauctores.pioneers.Constants.FX_SCHEDULER;

public class LobbyController implements Controller {

    private final App app;
    private final LoginService loginService;
    private final Provider<LoginController> loginController;
    private final Provider<ChatController> chatController;

    @FXML public Label gameLabel;

    @FXML public ListView gameListView;

    @FXML public Label playerLabel;

    @FXML public ListView playerListView;

    @FXML public Button createGameButton;

    @FXML public Button chatButton;

    @FXML public Button quitButton;

    private CompositeDisposable disposables = new CompositeDisposable();

    @Inject
    public LobbyController(App app, LoginService loginService, Provider<LoginController> loginController, Provider<ChatController> chatController){
        this.app = app;
        this.loginService = loginService;
        this.loginController = loginController;
        this.chatController = chatController;
    }


    public void init(){

    }

    public void destroy(){
        if (disposables != null) {
            disposables.dispose();
            disposables = null;
        }
    }

    public Parent render(){
        final FXMLLoader loader = new FXMLLoader(Main.class.getResource("views/lobbyScreen.fxml"));
        loader.setControllerFactory(c -> this);
        final Parent parent;
        try {
            parent = loader.load();
        }catch(IOException e) {
            e.printStackTrace();
            return null;
        }
        return parent;
    }

    public void toCreateGame(ActionEvent event) {

    }


    public void toChat(ActionEvent event) {

        final ChatController controller = chatController.get();
        app.show(controller);
    }

    public void quit(ActionEvent event) {
        disposables.add(loginService.logout()
                .subscribeOn(FX_SCHEDULER)
                .subscribe(() -> app.show(loginController.get()),
                        // TODO Show error message
                        Throwable::printStackTrace));
    }
}
