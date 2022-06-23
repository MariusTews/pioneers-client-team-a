package com.aviumauctores.pioneers.controller;

import javafx.event.ActionEvent;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;

public class TradeRequestController implements Controller{
    public Label tradeWoodLabel;
    public Label tradeBreadLabel;
    public Label tradeClayLabel;
    public Label tradeStoneLabel;
    public Label tradeWoolLabel;
    public Label getWoodLabel;
    public Label getBreadLabel;
    public Label getClayLabel;
    public Label getStoneLabel;
    public Label getWoolLabel;
    public ImageView playerAvatar;
    public Label playerLabel;
    public Button acceptButton;
    public Button declineButton;

    @Override
    public void init() {

    }

    @Override
    public void destroy(boolean closed) {

    }

    @Override
    public Parent render() {
        return null;
    }

    public void acceptRequest(ActionEvent actionEvent) {
    }

    public void declineRequest(ActionEvent actionEvent) {
    }
}
