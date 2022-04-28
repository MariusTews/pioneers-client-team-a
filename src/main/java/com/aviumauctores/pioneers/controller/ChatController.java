package com.aviumauctores.pioneers.controller;

import com.aviumauctores.pioneers.Main;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;

import java.io.IOException;

public class ChatController implements Controller {

    @FXML public TextField chatTextField;
    @FXML public Button sendButton;
    @FXML public ListView onlinePlayerList;
    @FXML public Button leaveButton;
    @FXML public ScrollBar chatScrollBar;
    @FXML public Label hintLabel;
    @FXML public Label onlinePlayerLabel;
    @FXML public Tab allTab;
    @FXML public TabPane chatTabPane;

    public void init(){

    }

    public void destroy(){

    }

    public Parent render() {
        final FXMLLoader loader = new FXMLLoader(Main.class.getResource("views/ChatScreen.fxml"));
        loader.setControllerFactory(c -> this);
        final Parent parent;
        try {
            parent = loader.load();
        } catch(IOException e) {
            e.printStackTrace();
            return null;
        }
        return parent;
    }
}
