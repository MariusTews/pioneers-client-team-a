package com.aviumauctores.pioneers;

import javafx.stage.Stage;
import org.testfx.framework.junit5.ApplicationTest;

class AppTest extends ApplicationTest {
    @Override
    public void start(Stage stage){
        final App app = new App(null);
        MainComponent testComponent = DaggerTestComponent.builder().mainApp(app).build();
        app.start(stage);
        app.show(testComponent.loginController());
    }
}