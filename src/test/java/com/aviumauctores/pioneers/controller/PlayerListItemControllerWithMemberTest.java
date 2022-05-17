package com.aviumauctores.pioneers.controller;

import com.aviumauctores.pioneers.App;
import com.aviumauctores.pioneers.model.Member;
import com.aviumauctores.pioneers.model.User;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.Parent;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testfx.framework.junit5.ApplicationTest;

import static org.junit.jupiter.api.Assertions.*;
import static org.testfx.matcher.base.GeneralMatchers.typeSafeMatcher;

@ExtendWith(MockitoExtension.class)
public class PlayerListItemControllerWithMemberTest extends ApplicationTest {
    @Mock
    PlayerListController parentController;

    @Spy
    User user = new User("42", "Player1", "online", null);

    @Spy
    Member gameMember = new Member("", "", "12", "42", false);

    @Mock
    ObservableList<Parent> items;

    @InjectMocks
    PlayerListItemController playerListItemController;

    @Override
    public void start(Stage stage) throws Exception {
        new App(playerListItemController).start(stage);
    }

    @Test
    void onGameMemberUpdatedNotReadyToReady() {
        // The view does contain only to image views, the avatar and the ready icon
        // The avatar has fitHeight set to 40,
        // so when this attribute is less than 40 the image view must be the ready view
        final ImageView readyView = lookup(typeSafeMatcher(ImageView.class, "Find readyView",
                imageView -> imageView.getFitHeight() < 40.0)).query();
        final Image image1 = readyView.getImage();
        final Color sampleColor1 = image1.getPixelReader().getColor(16, 0);
        // Ensure an image is shown
        assertNotNull(image1);

        Member newGameMember = new Member("", "", "12", "42", true);
        Platform.runLater(() -> {
            int incr = playerListItemController.onGameMemberUpdated(newGameMember);

            final Image image2 = readyView.getImage();
            final Color sampleColor2 = image2.getPixelReader().getColor(16, 0);
            // Check the increment is 1
            assertEquals(incr, 1);
            // Check the color has been changed
            assertNotEquals(sampleColor1, sampleColor2);
        });
    }

    @Test
    void onGameMemberUpdatedNoReadyChange() {
        final ImageView readyView = lookup(typeSafeMatcher(ImageView.class, "Find readyView",
            imageView -> imageView.getFitHeight() < 40.0)).query();
        final Image image1 = readyView.getImage();
        final Color sampleColor1 = image1.getPixelReader().getColor(16, 0);
        // Ensure an image is shown
        assertNotNull(image1);

        Member newGameMember = new Member("", "", "12", "42", false);
        Platform.runLater(() -> {
            int incr = playerListItemController.onGameMemberUpdated(newGameMember);

            final Image image2 = readyView.getImage();
            final Color sampleColor2 = image2.getPixelReader().getColor(16, 0);
            // Check the increment is 0
            assertEquals(incr, 0);
            // Check the color has not been changed
            assertEquals(sampleColor1, sampleColor2);
        });
    }
}
