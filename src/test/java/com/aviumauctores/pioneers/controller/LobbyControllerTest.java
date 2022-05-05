package com.aviumauctores.pioneers.controller;

import com.aviumauctores.pioneers.App;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.Extension;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testfx.api.FxAssert;
import org.testfx.framework.junit5.ApplicationTest;

@ExtendWith(MockitoExtension.class)
class LobbyControllerTest extends ApplicationTest {

    @Mock
    App app;

    @InjectMocks
    LobbyController lobbyController;

    @Override
    public void start(Stage stage) throws Exception {
        new App(lobbyController).start(stage);
    }


    @Test
    void chatButtonTest() {
        //TODO: chatButtonTest
        clickOn("#chatButton");


    }
}