package com.aviumauctores.pioneers.controller;

import com.aviumauctores.pioneers.App;
import com.aviumauctores.pioneers.model.User;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testfx.framework.junit5.ApplicationTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PlayerListItemControllerTest extends ApplicationTest {
    @Mock
    PlayerListController parentController;

    @Spy
    User user = new User("42", "Player1", "online", null,null);

    @Mock
    ObservableList<Parent> items;

    @InjectMocks
    PlayerListItemController playerListItemController;

    private Parent root;

    @Override
    public void start(Stage stage) throws Exception {
        new App(playerListItemController).start(stage);
        root = stage.getScene().getRoot();
    }

    @Test
    void onItemClicked() {
        clickOn(root);
        clickOn(root, MouseButton.SECONDARY);
        clickOn(root, MouseButton.MIDDLE);
        doubleClickOn(root, MouseButton.SECONDARY);
        doubleClickOn(root, MouseButton.MIDDLE);
        // Ensure a single click or a double click with a non-primary button does nothing
        verify(parentController, never()).onPlayerItemClicked(user);

        doubleClickOn(root);
        // Only a double click with the primary mouse button should work
        verify(parentController, atLeast(1)).onPlayerItemClicked(user);
    }

    @Test
    void onPlayerUpdated() {
        final Label playerNameLabel = lookup(user.name()).query();
        // Check the label shows the username
        assertEquals(playerNameLabel.getText(), user.name());

        User newUser = new User("42", "Player2", "online", null,null);
        Platform.runLater(() -> {
            playerListItemController.onPlayerUpdated(newUser);

            // Ensure the label now shows the new username
            assertEquals(playerNameLabel.getText(), newUser.name());
        });
    }
}